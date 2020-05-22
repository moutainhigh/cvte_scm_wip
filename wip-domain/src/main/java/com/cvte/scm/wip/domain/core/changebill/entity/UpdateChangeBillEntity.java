package com.cvte.scm.wip.domain.core.changebill.entity;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 12:51
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public class UpdateChangeBillEntity extends ChangeBillEntity {

    public UpdateChangeBillEntity(ChangeBillRepository changeBillRepository) {
        super(changeBillRepository);
    }

    @Override
    public ReqInstructionBuildVO parseChangeBill(ChangeReqVO reqHeaderVO) {
        ReqInstructionBuildVO instructionBuildVO = ReqInstructionBuildVO.buildVO(this);
        instructionBuildVO.setInstructionHeaderId(UUIDUtils.get32UUID())
                .setInstructionHeaderStatus(reqHeaderVO.getBillStatus())
                .setAimHeaderId(reqHeaderVO.getHeaderId())
                .setAimReqLotNo(reqHeaderVO.getSourceLotNo());
        return instructionBuildVO;
    }

    public static UpdateChangeBillEntity get() {
        return DomainFactory.get(UpdateChangeBillEntity.class);
    }

}
