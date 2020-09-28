package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrSubInvRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/27 20:02
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class CheckReqLotIssuedService {

    private WipReqLotIssuedRepository wipReqLotIssuedRepository;
    private QueryReqLineService queryReqLineService;
    private WipReqHeaderService wipReqHeaderService;
    private ScmItemService scmItemService;
    private WipMtrSubInvRepository wipMtrSubInvRepository;

    public CheckReqLotIssuedService(WipReqLotIssuedRepository wipReqLotIssuedRepository, QueryReqLineService queryReqLineService, WipReqHeaderService wipReqHeaderService, ScmItemService scmItemService, WipMtrSubInvRepository wipMtrSubInvRepository) {
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
        this.queryReqLineService = queryReqLineService;
        this.wipReqHeaderService = wipReqHeaderService;
        this.scmItemService = scmItemService;
        this.wipMtrSubInvRepository = wipMtrSubInvRepository;
    }

    /**
     * 校验领料批次数量
     *
     * @param wipReqLotIssued 领料数据
     * @author xueyuting
     * @since 2020/2/04 3:07 下午
     */
    public void verifyIssuedQty(WipReqLotIssuedEntity wipReqLotIssued) {
        // 已领料数据
        WipReqLotIssuedEntity queryLotEntity = new WipReqLotIssuedEntity().setHeaderId(wipReqLotIssued.getHeaderId())
                .setOrganizationId(wipReqLotIssued.getOrganizationId()).setItemNo(wipReqLotIssued.getItemNo()).setWkpNo(wipReqLotIssued.getWkpNo()).setStatus(StatusEnum.NORMAL.getCode());
        List<WipReqLotIssuedEntity> lotIssuedList = wipReqLotIssuedRepository.selectList(queryLotEntity);
        if (lotIssuedList == null) {
            lotIssuedList = new ArrayList<>();
        }

        // 替换旧的
        Iterator<WipReqLotIssuedEntity> iterator = lotIssuedList.iterator();
        while (iterator.hasNext()) {
            WipReqLotIssuedEntity lotIssued = iterator.next();
            if (lotIssued.getMtrLotNo().equals(wipReqLotIssued.getMtrLotNo())) {
                // 更新ID, 用于保存
                wipReqLotIssued.setId(lotIssued.getId());
                iterator.remove();
            }
        }
        long oldTotalLotIssuedQty = lotIssuedList.stream().mapToLong(WipReqLotIssuedEntity::getIssuedQty).sum();
        lotIssuedList.add(wipReqLotIssued);

        long totalReqQty = this.getItemReqQty(wipReqLotIssued);
        if (Objects.isNull(wipReqLotIssued.getIssuedQty())) {
            wipReqLotIssued.setIssuedQty(totalReqQty - oldTotalLotIssuedQty);
        } else {
            long totalIssuedQty = lotIssuedList.stream().mapToLong(WipReqLotIssuedEntity::getIssuedQty).sum();
            if (totalIssuedQty > totalReqQty) {
                log.info("领料数量大于需求数量,新增领料数量失败");
                throw new ParamsIncorrectException("领料数量大于需求数量");
            }
        }
    }

    public void checkLotValid(List<WipReqLotIssuedEntity> itemLotIssuedList) {
        if (ListUtil.empty(itemLotIssuedList)) {
            return;
        }
        List<WipMtrSubInvVO> mtrSubInvVOList = this.getMtrSubInv(itemLotIssuedList);

        Set<String> itemLotSet = itemLotIssuedList.stream().map(WipReqLotIssuedEntity::getMtrLotNo).collect(Collectors.toSet());
        Set<String> invLotSet = mtrSubInvVOList.stream().map(WipMtrSubInvVO::getLotNumber).collect(Collectors.toSet());
        itemLotSet.removeIf(invLotSet::contains);
        if (CollectionUtils.isNotEmpty(itemLotSet)) {
            throw new ParamsIncorrectException(String.format("库存中没有以下批次:%s;", String.join(",", itemLotSet)));
        }
    }

    public void checkIssuedQty(List<WipReqLotIssuedEntity> itemLotIssuedList) {
        long reqQty = this.getItemReqQty(itemLotIssuedList.get(0));
        long totalMtrQty = itemLotIssuedList.stream().mapToLong(WipReqLotIssuedEntity::getIssuedQty).sum();
        if (reqQty != totalMtrQty) {
            throw new ParamsIncorrectException(String.format("批次总投料量%d必须等于物料需求数量%d", totalMtrQty, reqQty));
        }
    }

    private long getItemReqQty(WipReqLotIssuedEntity wipReqLotIssued) {
        // 需求数据
        WipReqLineKeyQueryVO keyQueryVO = new WipReqLineKeyQueryVO();
        keyQueryVO.setHeaderId(wipReqLotIssued.getHeaderId())
                .setOrganizationId(wipReqLotIssued.getOrganizationId())
                .setItemNo(wipReqLotIssued.getItemNo())
                .setWkpNo(wipReqLotIssued.getWkpNo());
        List<WipReqLineEntity> reqLinesList = queryReqLineService.getValidLine(keyQueryVO);

        return reqLinesList.stream().mapToLong(WipReqLineEntity::getReqQty).sum();
    }

    private List<WipMtrSubInvVO> getMtrSubInv(List<WipReqLotIssuedEntity> itemLotIssuedList) {
        WipReqLotIssuedEntity randomEntity = itemLotIssuedList.get(0);
        // 获取工厂
        WipReqHeaderEntity reqHeaderEntity = wipReqHeaderService.getByHeaderId(randomEntity.getHeaderId());
        if (Objects.isNull(reqHeaderEntity)) {
            throw new ParamsIncorrectException(String.format("找不到ID为%s的工单", randomEntity.getHeaderId()));
        }
        String factoryId = reqHeaderEntity.getFactoryId();

        // 获取物料ID
        String itemId = scmItemService.getItemId(randomEntity.getItemNo());
        if (Objects.isNull(itemId)) {
            throw new ParamsIncorrectException(String.format("找不到编号为%s的物料", randomEntity.getItemNo()));
        }

        // 获取批次
        List<WipMtrSubInvVO> queryList = new ArrayList<>();
        String organizationId = randomEntity.getOrganizationId();
        for (WipReqLotIssuedEntity lotIssuedEntity : itemLotIssuedList) {
            WipMtrSubInvVO mtrSubInvVO = new WipMtrSubInvVO();
            mtrSubInvVO.setOrganizationId(organizationId)
                    .setFactoryId(factoryId)
                    .setInventoryItemId(itemId)
                    .setLotNumber(lotIssuedEntity.getMtrLotNo());
            queryList.add(mtrSubInvVO);
        }
        return wipMtrSubInvRepository.selectByVO(queryList);
    }

}
