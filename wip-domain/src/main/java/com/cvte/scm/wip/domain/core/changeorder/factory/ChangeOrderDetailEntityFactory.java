package com.cvte.scm.wip.domain.core.changeorder.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.changeorder.entity.ChangeOrderDetailEntity;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderDetailBuildVO;
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
public class ChangeOrderDetailEntityFactory implements DomainFactory<ChangeOrderDetailBuildVO, ChangeOrderDetailEntity> {

    @Override
    public ChangeOrderDetailEntity perfect(ChangeOrderDetailBuildVO vo) {
        ChangeOrderDetailEntity entity = ChangeOrderDetailEntity.get();
        entity.setDetailId(vo.getDetailId())
                .setBillId(vo.getBillId())
                .setMoLotNo(vo.getMoLotNo())
                .setOrganizationId(vo.getOrganizationId())
                .setWkpNo(vo.getWkpNo())
                .setItemIdOld(vo.getItemIdOld())
                .setItemIdNew(vo.getItemIdNew())
                .setOperationType(vo.getOperationType())
                .setPosNo(vo.getPosNo())
                .setEnableDate(vo.getEnableDate())
                .setDisableDate(vo.getDisableDate());
        return entity;
    }
}
