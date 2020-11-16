package com.cvte.scm.wip.domain.core.requirement.factory;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:27
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ReqInsEntityFactory implements DomainFactory<ReqInsBuildVO, ReqInsEntity> {

    private WipReqHeaderRepository wipReqHeaderRepository;
    private ScmItemService itemService;

    public ReqInsEntityFactory(WipReqHeaderRepository wipReqHeaderRepository, ScmItemService itemService) {
        this.wipReqHeaderRepository = wipReqHeaderRepository;
        this.itemService = itemService;
    }

    @Override
    public ReqInsEntity perfect(ReqInsBuildVO vo) {
        ReqInsEntity headerEntity = ReqInsEntity.get();
        headerEntity.setInsHeaderId(vo.getInsHeaderId())
                .setSourceChangeBillId(vo.getSourceChangeBillId())
                .setStatus(vo.getInsHeaderStatus())
                .setAimHeaderId(vo.getAimHeaderId())
                .setAimReqLotNo(vo.getAimReqLotNo())
                .setEnableDate(vo.getEnableDate())
                .setDisableDate(vo.getDisableDate())
                .setInvalidBy(vo.getInvalidBy())
                .setInvalidReason(vo.getInvalidReason());
        return headerEntity;
    }

    public ReqInsEntity perfect(List<WipReqLineEntity> reqLineList) {
        List<String> headerIdList = reqLineList.stream().map(WipReqLineEntity::getHeaderId).distinct().collect(Collectors.toList());
        if (ListUtil.empty(headerIdList)) {
            throw new ParamsIncorrectException("headerId不可为空");
        }
        if (headerIdList.size() > 1) {
            throw new ParamsIncorrectException("不允许同时变更多个单据");
        }
        String headerId = headerIdList.get(0);

        WipReqHeaderEntity reqHeader = wipReqHeaderRepository.getByHeaderId(headerId);
        if (Objects.isNull(reqHeader)) {
            throw new ParamsIncorrectException("投料单不存在");
        }
        String aimReqLotNo = reqHeader.getSourceLotNo();

        Long changeQty = reqLineList.get(0).getReqQty();
        if (null == changeQty || 0 == changeQty) {
            throw new ParamsIncorrectException("变更数量不能为0");
        }
        InsOperationTypeEnum operationTypeEnum;
        if (changeQty < 0) {
            operationTypeEnum = InsOperationTypeEnum.REDUCE;
        } else {
            operationTypeEnum = InsOperationTypeEnum.INCREASE;
        }

        ReqInsEntity ins = ReqInsEntity.get();
        String insHeaderId = UUIDUtils.get32UUID();
        ins.setInsHeaderId(insHeaderId)
                .setAimHeaderId(headerId);

        String itemNo = reqLineList.get(0).getItemNo();
        String itemId = itemService.getItemId(itemNo);
        List<ReqInsDetailEntity> insDetailList = new ArrayList<>(reqLineList.size());
        for (WipReqLineEntity changeLine : reqLineList) {
            ReqInsDetailEntity insDetail = ReqInsDetailEntity.get();
            insDetail.setInsDetailId(UUIDUtils.get32UUID())
                    .setAimHeaderId(headerId)
                    .setAimReqLotNo(aimReqLotNo)
                    .setOrganizationId(changeLine.getOrganizationId())
                    .setWkpNo(changeLine.getWkpNo())
                    .setMoLotNo(changeLine.getLotNumber())
                    .setPosNo(changeLine.getPosNo())
                    .setItemIdOld(itemId)
                    .setItemIdNew(itemId)
                    .setItemNoOld(itemNo)
                    .setItemNoNew(itemNo)
                    .setItemQty(BigDecimal.valueOf(changeLine.getReqQty()).abs())
                    .setItemUnitQty(BigDecimal.valueOf(changeLine.getUnitQty()).abs())
                    .setOperationType(operationTypeEnum.getCode());
            insDetailList.add(insDetail);
        }
        ins.setDetailList(insDetailList);

        return ins;
    }

}
