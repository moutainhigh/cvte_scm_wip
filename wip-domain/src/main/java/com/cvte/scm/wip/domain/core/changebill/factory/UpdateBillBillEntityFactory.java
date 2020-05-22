package com.cvte.scm.wip.domain.core.changebill.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.changebill.entity.UpdateChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:17
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class UpdateBillBillEntityFactory implements DomainFactory<ChangeBillBuildVO, UpdateChangeBillEntity> {

    @Override
    public UpdateChangeBillEntity perfect(ChangeBillBuildVO vo) {
        UpdateChangeBillEntity entity = UpdateChangeBillEntity.get();
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
