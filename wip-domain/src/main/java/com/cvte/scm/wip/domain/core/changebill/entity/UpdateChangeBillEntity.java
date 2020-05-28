package com.cvte.scm.wip.domain.core.changebill.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 12:51
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateChangeBillEntity extends ChangeBillEntity {

    public UpdateChangeBillEntity(ChangeBillRepository changeBillRepository) {
        super(changeBillRepository);
    }

    @Override
    public ReqInsBuildVO parseChangeBill(ChangeReqVO reqHeaderVO) {
        ReqInsBuildVO insBuildVO = super.parseChangeBill(reqHeaderVO);
        ReqInsEntity insEntity = ReqInsEntity.get().getByKey(this.getBillId());
        if (Objects.nonNull(insEntity)) {
            insBuildVO.setInsHeaderId(insEntity.getInsHeaderId());
            insBuildVO.getDetailList().forEach(detailBuildVO -> detailBuildVO.setInsHeaderId(insEntity.getInsHeaderId()));
        }
        insBuildVO.setChangeType(ChangedTypeEnum.UPDATE.getCode());
        if (StatusEnum.CLOSE.getCode().equals(this.getBillStatus())) {
            insBuildVO.setChangeType(ChangedTypeEnum.DELETE.getCode());
        }
        return insBuildVO;
    }

    public static UpdateChangeBillEntity get() {
        return DomainFactory.get(UpdateChangeBillEntity.class);
    }

}
