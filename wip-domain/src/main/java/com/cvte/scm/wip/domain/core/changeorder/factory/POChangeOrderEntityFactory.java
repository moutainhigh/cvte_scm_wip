package com.cvte.scm.wip.domain.core.changeorder.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.changeorder.entity.POChangeOrderEntity;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 16:10
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class POChangeOrderEntityFactory implements DomainFactory<ChangeOrderBuildVO, POChangeOrderEntity> {

    @Override
    public POChangeOrderEntity perfect(ChangeOrderBuildVO vo) {
        POChangeOrderEntity entity = POChangeOrderEntity.get();
        entity.setBillId(vo.getBillId())
                .setBillNo(vo.getBillNo())
                .setOrganizationId(vo.getOrganizationId())
                .setBillType(vo.getBillType())
                .setMoId(vo.getMoId())
                .setBillStatus(vo.getBillStatus())
                .setEnableDate(vo.getEnableDate())
                .setDisableDate(vo.getDisableDate());
        return entity;
    }

}
