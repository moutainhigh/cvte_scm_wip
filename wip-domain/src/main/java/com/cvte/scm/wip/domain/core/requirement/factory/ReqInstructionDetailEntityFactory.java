package com.cvte.scm.wip.domain.core.requirement.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionDetailBuildVO;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ReqInstructionDetailEntityFactory implements DomainFactory<ReqInstructionDetailBuildVO, ReqInstructionDetailEntity> {

    @Override
    public ReqInstructionDetailEntity perfect(ReqInstructionDetailBuildVO vo) {
        ReqInstructionDetailEntity detailEntity = ReqInstructionDetailEntity.get();
        detailEntity.setInstructionDetailId(vo.getInstructionDetailId())
                .setOrganizationId(vo.getOrganizationId())
                .setSourceChangeDetailId(vo.getSourceChangeDetailId())
                .setMoLotNo(vo.getMoLotNo())
                .setItemIdOld(vo.getItemIdOld())
                .setItemIdNew(vo.getItemIdNew())
                .setWkpNo(vo.getWkpNo())
                .setPosNo(vo.getPosNo())
                .setItemQty(vo.getItemQty())
                .setItemUnitQty(vo.getItemUnitQty())
                .setOperationType(vo.getOperationType())
                .setInsStatus(vo.getInsStatus())
                .setEnableDate(vo.getEnableDate())
                .setDisableDate(vo.getDisableDate());
        return detailEntity;
    }

}
