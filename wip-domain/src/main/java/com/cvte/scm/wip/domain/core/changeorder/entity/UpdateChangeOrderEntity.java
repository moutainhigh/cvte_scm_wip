package com.cvte.scm.wip.domain.core.changeorder.entity;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.changeorder.repository.ChangeOrderRepository;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.enums.ChangeOrderStatusEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 12:51
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public class UpdateChangeOrderEntity extends ChangeOrderEntity {

    public UpdateChangeOrderEntity(ChangeOrderRepository changeOrderRepository) {
        super(changeOrderRepository);
    }

    @Override
    public ReqInstructionBuildVO parseChangeOrder(ChangeReqVO reqHeaderVO) {
        ReqInstructionBuildVO instructionBuildVO = ReqInstructionBuildVO.buildVO(this);
        instructionBuildVO.setInstructionHeaderId(UUIDUtils.get32UUID())
                .setInstructionHeaderStatus(reqHeaderVO.getBillStatus())
                .setAimHeaderId(reqHeaderVO.getHeaderId())
                .setAimReqLotNo(reqHeaderVO.getSourceLotNo());
        if (ChangeOrderStatusEnum.UNDO.getCode().equals(reqHeaderVO.getBillStatus())) {
            instructionBuildVO.setExecuteType(ChangedTypeEnum.DELETE);
            return instructionBuildVO;
        }
        instructionBuildVO.setExecuteType(ChangedTypeEnum.UPDATE);
        return instructionBuildVO;
    }

    public static UpdateChangeOrderEntity get() {
        return DomainFactory.get(UpdateChangeOrderEntity.class);
    }

}
