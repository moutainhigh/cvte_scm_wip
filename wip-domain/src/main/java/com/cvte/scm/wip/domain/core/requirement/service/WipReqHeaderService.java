package com.cvte.scm.wip.domain.core.requirement.service;

import cn.hutool.core.bean.BeanUtil;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.deprecated.BaseBatchMapper;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.cvte.csb.toolkit.StringUtils.format;
import static com.cvte.csb.toolkit.StringUtils.isNotEmpty;
import static com.cvte.scm.wip.common.utils.EntityUtils.getEntityPrintInfo;
import static java.util.Objects.isNull;

/**
 * 服务实现类
 *
 * @author author
 * @since 2019-12-30
 */
@Service
@Slf4j
@Transactional
public class WipReqHeaderService {

    /**
     * 一个位标识，用于获取枚举 {@link BillStatusEnum} 中 "草稿、已确定、已备料和已领料" 的 code 值列表。
     */
    private static final int DRAFT_CONFIRMED_PREPARED_ISSUED = 15;

    private BaseBatchMapper batchMapper;
    private WipReqHeaderRepository wipReqHeaderRepository;

    public WipReqHeaderService(@Qualifier("pgBatchMapper") BaseBatchMapper batchMapper, WipReqHeaderRepository wipReqHeaderRepository) {
        this.batchMapper = batchMapper;
        this.wipReqHeaderRepository = wipReqHeaderRepository;
    }

    public String updateWipReqHeaders(List<WipReqHeaderEntity> wipReqHeaderList, ExecutionModeEnum mode) {
        List<WipReqHeaderEntity> updateWipReqHeaders = new ArrayList<>();
        String errorMessage = EntityUtils.accumulate(wipReqHeaderList, header -> validateAndGetUpdateDataHelper(header, updateWipReqHeaders));
        EntityUtils.handleErrorMessage(errorMessage, mode);
        wipReqHeaderList.forEach(line -> EntityUtils.writeStdUpdInfoToEntity(line, CurrentContextUtils.getOrDefaultUserId("SCM-WIP")));
        batchMapper.update(wipReqHeaderList);
        return errorMessage;
    }

    private String validateAndGetUpdateDataHelper(WipReqHeaderEntity header, List<WipReqHeaderEntity> updateWipReqHeaders) {
        Example example;
        String errorMessage;
        List<WipReqHeaderEntity> wipReqHeaders;
        WipReqHeaderEntity dbHeader;
        Predicate<List<?>> invalid = headers -> CollectionUtils.isEmpty(headers) || headers.size() > 1;
        if (isNull(header)) {
            return "[待更新的数据存在空值]; ";
        } else if (isNotEmpty(errorMessage = validateIndex(header))) {
            return StringUtils.format("[更新数据存在错误] {}", errorMessage);
        } else if (isNull(example = createCustomExample(header)) || invalid.test(wipReqHeaders = wipReqHeaderRepository.selectByExample(example))) {
            return format("[数据不存在] 组织 = {}, 工单号 = {}; ", header.getOrganizationId(), header.getBillNo());
        } else if (!(dbHeader = wipReqHeaders.get(0)).getHeaderId().equals(header.getHeaderId())) {
            return format("[数据错误：header_id = {}] {}; ", dbHeader.getHeaderId(), getEntityPrintInfo(header));
        }
        BeanUtil.copyProperties(header, dbHeader, EntityUtils.IGNORE_NULL_VALUE_OPTION);
        updateWipReqHeaders.add(dbHeader);
        return "";
    }

    private String validateIndex(WipReqHeaderEntity wipReqHeader) {
        StringBuilder indexErrorMsg = new StringBuilder();
        BiFunction<String, String, String> format = (s1, s2) -> StringUtils.isEmpty(s1) ? s2 + "不可为空; " : "";
        indexErrorMsg.append(format.apply(wipReqHeader.getOrganizationId(), "组织ID"))
                .append(format.apply(wipReqHeader.getBillNo(), "工单编号"));
        BillStatusEnum status = CodeableEnumUtils.getCodeableEnumByCode(wipReqHeader.getBillStatus(), BillStatusEnum.class);
        if (status == BillStatusEnum.CLOSED || status == BillStatusEnum.CANCELLED) {
            indexErrorMsg.append(format("投料单行状态为{}; ", status.getDesc()));
        }
        return indexErrorMsg.toString();
    }

    private Example createCustomExample(WipReqHeaderEntity wipReqHeader) {
        Example example = new Example(WipReqHeaderEntity.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("organizationId", wipReqHeader.getOrganizationId())
                .andEqualTo("billNo", wipReqHeader.getBillNo())
                .andIn("billStatus", CodeableEnumUtils.getCodesByOrdinalFlag(DRAFT_CONFIRMED_PREPARED_ISSUED, BillStatusEnum.class));
        return criteria.isValid() ? example : null;
    }
}