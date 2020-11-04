package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.constants.CommonConstant;
import com.cvte.scm.wip.common.utils.LambdaUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipItemWkpPosEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.QueryWipItemWkpPosVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.QueryWipReqHeaderVO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 配料需求行拆分服务
 *
 * @author zy
 * @date 2020-05-30 10:20
 **/
@Service
public class WipReqLineSplitService {


    @Autowired
    private WipReqHeaderRepository wipReqHeaderRepository;

    @Autowired
    private WipItemWkpPosService wipItemWkpPosService;

    @Autowired
    private ModelMapper modelMapper;


    /**
     * 拆分订单行
     * <p>
     *     该方法不会做数据校验，对于问题订单行数据仅不拆分处理
     * </p>
     *
     * @param wipReqLineEntities
     * @return java.util.List<com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity>
     **/
    public List<WipReqLineEntity> splitByItemWkpPos(List<WipReqLineEntity> wipReqLineEntities) {
        if (CollectionUtils.isEmpty(wipReqLineEntities)) {
            return new ArrayList<>();
        }
        List<WipReqHeaderEntity> wipReqHeaderEntities = wipReqHeaderRepository.listWipReqHeaderEntity(new QueryWipReqHeaderVO()
                .setWipHeaderIds(LambdaUtils.mapToList(wipReqLineEntities, WipReqLineEntity::getHeaderId)));

        QueryWipItemWkpPosVO queryWipItemWkpPosVO = new QueryWipItemWkpPosVO()
                .setQueryDate(new Date())
                .setItemCodes(LambdaUtils.mapToList(wipReqLineEntities, WipReqLineEntity::getItemNo))
                .setProductModels(LambdaUtils.mapToList(wipReqHeaderEntities, WipReqHeaderEntity::getProductModel))
                .setOrganizationIds(LambdaUtils.mapToList(wipReqLineEntities, WipReqLineEntity::getOrganizationId));
        List<WipItemWkpPosEntity> wipItemWkpPosEntities = wipItemWkpPosService.listWipItemWkpPosEntity(queryWipItemWkpPosVO);
        return splitByItemWkpPos(wipReqLineEntities, wipItemWkpPosEntities, wipReqHeaderEntities);
    }

    private List<WipReqLineEntity> splitByItemWkpPos(List<WipReqLineEntity> wipReqLineEntities,
                                                     List<WipItemWkpPosEntity> wipItemWkpPosEntities,
                                                     List<WipReqHeaderEntity> wipReqHeaderEntities) {

        // 工艺属性分割辅助map
        Map<String, List<WipItemWkpPosEntity>> groupByRulesMap = new HashMap<>();
        for (WipItemWkpPosEntity entity : wipItemWkpPosEntities) {
            String key = entity.generateGroupKey();
            if (!groupByRulesMap.containsKey(key)) {
                groupByRulesMap.put(key, new ArrayList<>());
            }
            groupByRulesMap.get(key).add(entity);
        }

        Map<String, WipReqHeaderEntity> headerMap =
                wipReqHeaderEntities.stream().collect(Collectors.toMap(WipReqHeaderEntity::getHeaderId, Function.identity()));


        List<WipReqLineEntity> afterSplit = new ArrayList<>();
        for (WipReqLineEntity entity : wipReqLineEntities) {
            WipReqHeaderEntity wipReqHeaderEntity = headerMap.get(entity.getHeaderId());
            if (ObjectUtils.isNull(wipReqHeaderEntity)) {
                // 如果获取不到头数据，则不对数据做分割
                afterSplit.add(entity);
            }
            String groupKey = entity.getOrganizationId()
                    + CommonConstant.COMMON_SEPARATOR
                    + wipReqHeaderEntity.getProductModel()
                    + CommonConstant.COMMON_SEPARATOR
                    + entity.getItemNo();
            afterSplit.addAll(splitByItemWkpPos(entity, groupByRulesMap.get(groupKey)));
        }

        return afterSplit;
    }

    private List<WipReqLineEntity> splitByItemWkpPos(WipReqLineEntity wipReqLineEntity,
                                                     List<WipItemWkpPosEntity> wipItemWkpPosEntities) {
        if (ObjectUtils.isNull(wipReqLineEntity)
            || ObjectUtils.isNull(wipReqLineEntity.getReqQty())
            || CollectionUtils.isEmpty(wipItemWkpPosEntities)) {
            // 未获取到工艺属性配置，或数据有问题：不做分割，直接返回
            return Arrays.asList(wipReqLineEntity);
        }

        Long totalReqQty = wipReqLineEntity.getReqQty();
        Long allocatedReqQty = 0L;

        List<WipReqLineEntity> afterSplit = new ArrayList<>();
        for (int i = 0; i < wipItemWkpPosEntities.size(); i ++) {

            Long reqQty = i == wipItemWkpPosEntities.size() - 1 ? totalReqQty - allocatedReqQty : totalReqQty / wipItemWkpPosEntities.size();
            allocatedReqQty += reqQty;

            WipReqLineEntity cpEntity = new WipReqLineEntity();
            modelMapper.map(wipReqLineEntity, cpEntity);
            cpEntity.setLineId(UUIDUtils.getUUID()).setReqQty(reqQty);
            afterSplit.add(cpEntity);
        }
        return afterSplit;
    }

}
