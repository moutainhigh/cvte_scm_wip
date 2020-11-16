package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
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
    private WipLotOnHandService wipLotOnHandService;

    public CheckReqLotIssuedService(WipReqLotIssuedRepository wipReqLotIssuedRepository, QueryReqLineService queryReqLineService, WipReqHeaderService wipReqHeaderService, ScmItemService scmItemService, WipMtrSubInvRepository wipMtrSubInvRepository, WipLotOnHandService wipLotOnHandService) {
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
        this.queryReqLineService = queryReqLineService;
        this.wipReqHeaderService = wipReqHeaderService;
        this.scmItemService = scmItemService;
        this.wipMtrSubInvRepository = wipMtrSubInvRepository;
        this.wipLotOnHandService = wipLotOnHandService;
    }

    public void checkLotValid(List<WipReqLotIssuedEntity> itemLotIssuedList) {
        if (ListUtil.empty(itemLotIssuedList)) {
            return;
        }

        WipReqLotIssuedEntity randomEntity = itemLotIssuedList.get(0);
        String organizationId = randomEntity.getOrganizationId();
        // 物料ID
        String itemId = scmItemService.getItemId(randomEntity.getItemNo());
        if (Objects.isNull(itemId)) {
            throw new ParamsIncorrectException(String.format("找不到编号为%s的物料", randomEntity.getItemNo()));
        }
        // 物料批次
        List<String> lotNumbers = itemLotIssuedList.stream().map(WipReqLotIssuedEntity::getMtrLotNo).collect(Collectors.toList());

        // 库存
        List<WipMtrSubInvVO> mtrSubInvVOList = wipMtrSubInvRepository.selectByItem(organizationId, null, itemId, lotNumbers);

        Set<String> itemLotSet = itemLotIssuedList.stream().map(WipReqLotIssuedEntity::getMtrLotNo).collect(Collectors.toSet());
        Set<String> invLotSet = mtrSubInvVOList.stream().map(WipMtrSubInvVO::getLotNumber).collect(Collectors.toSet());
        itemLotSet.removeIf(invLotSet::contains);
        if (CollectionUtils.isNotEmpty(itemLotSet)) {
            // 在途
            List<WipMtrSubInvVO> lotOnHandList = wipLotOnHandService.getOnHand(organizationId, itemId, itemLotSet);
            Set<String> lotOnHandSet = lotOnHandList.stream().map(WipMtrSubInvVO::getLotNumber).collect(Collectors.toSet());
            itemLotSet.removeIf(lotOnHandSet::contains);
            if (CollectionUtils.isNotEmpty(itemLotSet)) {
                throw new ParamsIncorrectException(String.format("库存或在途中没有以下批次:%s;", String.join(",", itemLotSet)));
            }
        }
    }

    public void checkIssuedQty(List<WipReqLotIssuedEntity> itemLotIssuedList) {
        long reqQty = this.getItemReqQty(itemLotIssuedList.get(0));
        long totalMtrQty = itemLotIssuedList.stream().mapToLong(lot -> Optional.ofNullable(lot.getAssignQty()).orElse(BigDecimal.ZERO).longValue()).sum();
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

}
