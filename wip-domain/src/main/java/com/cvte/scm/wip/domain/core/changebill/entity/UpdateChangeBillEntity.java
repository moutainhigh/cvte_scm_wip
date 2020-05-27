package com.cvte.scm.wip.domain.core.changebill.entity;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 12:51
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class UpdateChangeBillEntity extends ChangeBillEntity {

    public UpdateChangeBillEntity(ChangeBillRepository changeBillRepository) {
        super(changeBillRepository);
    }

    @Override
    public ReqInstructionBuildVO parseChangeBill(ChangeReqVO reqHeaderVO) {
        ReqInstructionBuildVO instructionBuildVO = ReqInstructionBuildVO.buildVO(this);
        if (StringUtils.isNotBlank(this.getBillStatus()) && StatusEnum.CLOSE.getCode().equals(this.getBillStatus())) {
            // EBS作废更改单
            instructionBuildVO.setInstructionHeaderStatus(ProcessingStatusEnum.UNHANDLED.getCode());
        }

        instructionBuildVO.setAimHeaderId(reqHeaderVO.getHeaderId())
                .setAimReqLotNo(reqHeaderVO.getSourceLotNo());

        ReqInstructionEntity instructionEntity = ReqInstructionEntity.get().getByKey(this.getBillId());
        if (Objects.nonNull(instructionEntity)) {
            instructionBuildVO.setInstructionHeaderId(instructionEntity.getInstructionHeaderId());
        }
        return instructionBuildVO;
    }

    public static UpdateChangeBillEntity get() {
        return DomainFactory.get(UpdateChangeBillEntity.class);
    }

}
