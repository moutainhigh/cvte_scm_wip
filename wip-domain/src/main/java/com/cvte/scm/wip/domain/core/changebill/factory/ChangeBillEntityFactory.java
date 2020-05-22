package com.cvte.scm.wip.domain.core.changebill.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 14:33
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ChangeBillEntityFactory implements DomainFactory<ChangeBillBuildVO, ChangeBillEntity> {

    @Override
    public ChangeBillEntity perfect(ChangeBillBuildVO vo) {
        ChangeBillEntity entity = ChangeBillEntity.get();
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
