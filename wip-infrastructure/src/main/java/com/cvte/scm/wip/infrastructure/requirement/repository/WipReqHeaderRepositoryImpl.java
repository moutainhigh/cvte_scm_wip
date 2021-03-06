package com.cvte.scm.wip.infrastructure.requirement.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.rtc.valueobject.ScmLotControlVO;
import com.cvte.scm.wip.infrastructure.deprecated.BaseBatchMapper;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.QueryWipReqHeaderVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqHeaderMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqHeaderDO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.cvte.csb.toolkit.StringUtils.format;
import static com.cvte.csb.toolkit.StringUtils.isNotEmpty;
import static com.cvte.scm.wip.common.utils.EntityUtils.getEntityPrintInfo;
import static java.util.Objects.isNull;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/5/17 20:04
 */
@Repository
public class WipReqHeaderRepositoryImpl implements WipReqHeaderRepository {

    /**
     * 一个位标识，用于获取枚举 {@link BillStatusEnum} 中 "草稿、已确定、已备料和已领料" 的 code 值列表。
     */
    private static final int DRAFT_CONFIRMED_PREPARED_ISSUED = 15;

    private BaseBatchMapper batchMapper;
    private WipReqHeaderMapper wipReqHeaderMapper;

    public WipReqHeaderRepositoryImpl(@Qualifier("pgBatchMapper") BaseBatchMapper batchMapper, WipReqHeaderMapper wipReqHeaderMapper) {
        this.batchMapper = batchMapper;
        this.wipReqHeaderMapper = wipReqHeaderMapper;
    }

    @Override
    public String getSourceId(String headerId) {
        return wipReqHeaderMapper.getSourceId(headerId);
    }

    @Override
    public WipReqHeaderEntity getBySourceId(String sourceId) {
        if (StringUtils.isBlank(sourceId)) {
            throw new ParamsIncorrectException("来源工单ID不可为空");
        }
        WipReqHeaderDO queryDO = new WipReqHeaderDO().setSourceId(sourceId);
        WipReqHeaderDO resultHeader = wipReqHeaderMapper.selectOne(queryDO);
        if (Objects.isNull(resultHeader)) {
            return null;
        }
        return WipReqHeaderDO.buildEntity(resultHeader);
    }

    @Override
    public WipReqHeaderEntity getByHeaderId(String headerId) {
        Example example = new Example(WipReqHeaderDO.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("headerId", headerId);
        List<String> exceedStatus = new ArrayList<>();
        exceedStatus.add(BillStatusEnum.CLOSED.getCode());
        exceedStatus.add(BillStatusEnum.CANCELLED.getCode());
        criteria.andNotIn("billStatus", exceedStatus);
        List<WipReqHeaderDO> reqHeaderDOList = wipReqHeaderMapper.selectByExample(example);
        if (ListUtil.notEmpty(reqHeaderDOList)) {
            return WipReqHeaderDO.buildEntity(reqHeaderDOList.get(0));
        }
        return null;
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
    public List<WipReqHeaderEntity> selectAddedData(List<Integer> organizationIdList, String factoryId, List<ScmLotControlVO> scmLotControlVOList) {
        // 已发放
        List<WipReqHeaderDO> deliveredHeaderList = wipReqHeaderMapper.selectDelivered(organizationIdList, factoryId);
        List<WipReqHeaderDO> headerDOList = new ArrayList<>(deliveredHeaderList);

        // 特殊逻辑
        List<WipReqHeaderDO> specificHeaderList = wipReqHeaderMapper.selectSpecific(organizationIdList, factoryId, scmLotControlVOList);
        if (ListUtil.notEmpty(specificHeaderList)) {
            // 注意,不能随意调整过滤和去重的顺序
            List<String> specificHeaderIdList = specificHeaderList.stream().map(WipReqHeaderDO::getHeaderId).collect(Collectors.toList());

            // 过滤掉未发放中未刷新过MRP的
            List<String> cachedHeaderIdList = wipReqHeaderMapper.filterCachedUndelivered(specificHeaderIdList);
            specificHeaderList.removeIf(specificHeader -> !cachedHeaderIdList.contains(specificHeader.getHeaderId()));
            List<String> filteredSpecificHeaderIdList = specificHeaderList.stream().map(WipReqHeaderDO::getHeaderId).collect(Collectors.toList());

            // 去重, 例:视昱更改单生成的非标工单是已发放的状态
            headerDOList.removeIf(deliveredHeader -> filteredSpecificHeaderIdList.contains(deliveredHeader.getHeaderId()));

            headerDOList.addAll(specificHeaderList);
        }
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

    @Override
    public boolean existLotNumber(String headerId, String lotNumber) {
        if (!NumberUtil.isInteger(headerId)) {
            return false;
        }
        return wipReqHeaderMapper.existLotNumber(Integer.parseInt(headerId), lotNumber);
    }

    @Override
    public List<WipReqHeaderEntity> listWipReqHeaderEntity(QueryWipReqHeaderVO queryWipReqHeaderVO) {
        if (CollectionUtils.isEmpty(queryWipReqHeaderVO.getWipHeaderIds())) {
            return Collections.emptyList();
        }
        Example example = new Example(WipReqHeaderDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("headerId", queryWipReqHeaderVO.getWipHeaderIds());
        return WipReqHeaderDO.batchBuildEntity(wipReqHeaderMapper.selectByExample(example));
    }

    @Override
    public WipReqHeaderEntity selectByMoNo(String moNo) {
        if (StringUtils.isBlank(moNo)) {
            throw new ParamsIncorrectException("工单号不可为空");
        }
        Example example = new Example(WipReqHeaderDO.class);
        example.createCriteria()
                .andEqualTo("sourceNo", moNo);
        List<WipReqHeaderDO> reqHeaderDOList = wipReqHeaderMapper.selectByExample(example);
        if (ListUtil.notEmpty(reqHeaderDOList)) {
            return WipReqHeaderDO.buildEntity(reqHeaderDOList.get(0));
        }
        return null;
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
