package com.cvte.scm.wip.infrastructure.requirement.repository;

import cn.hutool.core.bean.BeanUtil;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.deprecated.BaseBatchMapper;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqHeaderMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqHeaderDO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.cvte.csb.toolkit.StringUtils.format;
import static com.cvte.csb.toolkit.StringUtils.isNotEmpty;
import static com.cvte.scm.wip.common.utils.EntityUtils.getEntityPrintInfo;
import static java.util.Objects.isNull;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:04
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqHeaderRepositoryImpl implements WipReqHeaderRepository {

    /**
     * 一个位标识，用于获取枚举 {@link BillStatusEnum} 中 "草稿、已确定、已备料和已领料" 的 code 值列表。
     */
    private static final int DRAFT_CONFIRMED_PREPARED_ISSUED = 15;

    private BaseBatchMapper batchMapper;
    private WipReqHeaderMapper  wipReqHeaderMapper;

    public WipReqHeaderRepositoryImpl(@Qualifier("pgBatchMapper") BaseBatchMapper batchMapper, WipReqHeaderMapper wipReqHeaderMapper) {
        this.batchMapper = batchMapper;
        this.wipReqHeaderMapper = wipReqHeaderMapper;
    }

    @Override
    public String getSourceId(String headerId) {
        return wipReqHeaderMapper.getSourceId(headerId);
    }

    @Override
    public List<WipReqHeaderEntity> selectList(WipReqHeaderEntity headerEntity) {
        WipReqHeaderDO queryDO = WipReqHeaderDO.buildDO(headerEntity);
        List<WipReqHeaderDO> headerDOList = wipReqHeaderMapper.select(queryDO);
        return WipReqHeaderDO.batchBuildEntity(headerDOList);
    }

    @Override
    public List<WipReqHeaderEntity> selectByExample(Example example) {
        List<WipReqHeaderDO> headerDOList = wipReqHeaderMapper.selectByExample(example);
        return WipReqHeaderDO.batchBuildEntity(headerDOList);
    }

    @Override
    public List<WipReqHeaderEntity> selectAddedData(List<Integer> organizationIdList) {
        List<WipReqHeaderDO> headerDOList = wipReqHeaderMapper.selectAddedData(organizationIdList);
        return WipReqHeaderDO.batchBuildEntity(headerDOList);
    }

    @Override
    public String validateAndGetUpdateDataHelper(WipReqHeaderEntity header, List<WipReqHeaderEntity> updateWipReqHeaders) {
        Example example;
        String errorMessage;
        List<WipReqHeaderDO> wipReqHeaders;
        WipReqHeaderDO dbHeader;
        Predicate<List<?>> invalid = headers -> CollectionUtils.isEmpty(headers) || headers.size() > 1;
        if (isNull(header)) {
            return "[待更新的数据存在空值]; ";
        } else if (isNotEmpty(errorMessage = validateIndex(header))) {
            return StringUtils.format("[更新数据存在错误] {}", errorMessage);
        } else if (isNull(example = createCustomExample(header)) || invalid.test(wipReqHeaders = wipReqHeaderMapper.selectByExample(example))) {
            return format("[数据不存在] 组织 = {}, 工单号 = {}; ", header.getOrganizationId(), header.getBillNo());
        } else if (!(dbHeader = wipReqHeaders.get(0)).getHeaderId().equals(header.getHeaderId())) {
            return format("[数据错误：header_id = {}] {}; ", dbHeader.getHeaderId(), getEntityPrintInfo(header));
        }
        BeanUtil.copyProperties(header, dbHeader, EntityUtils.IGNORE_NULL_VALUE_OPTION);
        updateWipReqHeaders.add(WipReqHeaderDO.buildEntity(dbHeader));
        return "";
    }

    @Override
    public void batchInsert(List<WipReqHeaderEntity> headerEntityList) {
        List<WipReqHeaderDO> headerDOList = WipReqHeaderDO.batchBuildDO(headerEntityList);
        batchMapper.insert(headerDOList);
    }

    @Override
    public void batchUpdate(List<WipReqHeaderEntity> headerEntityList) {
        List<WipReqHeaderDO> headerDOList = WipReqHeaderDO.batchBuildDO(headerEntityList);
        batchMapper.update(headerDOList);
    }

    @Override
    public void updateStatusById(String billStatus, String headerId) {
        WipReqHeaderDO header = new WipReqHeaderDO().setBillStatus(BillStatusEnum.PREPARED.getCode());
        EntityUtils.writeStdUpdInfoToEntity(header, EntityUtils.getWipUserId());
        Example example = new Example(WipReqHeaderDO.class);
        example.createCriteria().andEqualTo("headerId", headerId);
        wipReqHeaderMapper.updateByExampleSelective(header, example);
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
        Example example = new Example(WipReqHeaderDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("organizationId", wipReqHeader.getOrganizationId())
                .andEqualTo("billNo", wipReqHeader.getBillNo())
                .andIn("billStatus", CodeableEnumUtils.getCodesByOrdinalFlag(DRAFT_CONFIRMED_PREPARED_ISSUED, BillStatusEnum.class));
        return criteria.isValid() ? example : null;
    }

}
