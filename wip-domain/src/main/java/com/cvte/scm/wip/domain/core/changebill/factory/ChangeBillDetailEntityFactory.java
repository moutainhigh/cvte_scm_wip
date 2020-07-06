package com.cvte.scm.wip.domain.core.changebill.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 15:29
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ChangeBillDetailEntityFactory implements DomainFactory<ChangeBillDetailBuildVO, ChangeBillDetailEntity> {

    @Override
    public ChangeBillDetailEntity perfect(ChangeBillDetailBuildVO vo) {
        ChangeBillDetailEntity entity = ChangeBillDetailEntity.get();
        entity.setDetailId(vo.getDetailId())
                .setBillId(vo.getBillId())
                .setMoLotNo(vo.getMoLotNo())
                .setStatus(vo.getStatus())
                .setOrganizationId(vo.getOrganizationId())
                .setWkpNo(vo.getWkpNo())
                .setItemIdOld(vo.getItemIdOld())
                .setItemIdNew(vo.getItemIdNew())
                .setOperationType(vo.getOperationType())
                .setPosNo(vo.getPosNo())
                .setItemQty(vo.getItemQty())
                .setItemUnitQty(vo.getItemUnitQty())
                .setEnableDate(vo.getEnableDate())
                .setDisableDate(vo.getDisableDate())
                .setSourceLineId(vo.getSourceLineId())
                .setIssueFlag(vo.getIssueFlag());
        return entity;
    }
}
