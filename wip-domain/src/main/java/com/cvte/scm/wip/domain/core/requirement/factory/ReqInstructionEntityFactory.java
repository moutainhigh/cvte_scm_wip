package com.cvte.scm.wip.domain.core.requirement.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:27
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ReqInstructionEntityFactory implements DomainFactory<ReqInstructionBuildVO, ReqInstructionEntity> {

    @Override
    public ReqInstructionEntity perfect(ReqInstructionBuildVO vo) {
        ReqInstructionEntity headerEntity = ReqInstructionEntity.get();
        headerEntity.setInstructionHeaderId(vo.getInstructionHeaderId())
                .setSourceChangeBillId(vo.getSourceChangeBillId())
                .setInstructionHeaderStatus(vo.getInstructionHeaderStatus())
                .setAimHeaderId(vo.getAimHeaderId())
                .setAimReqLotNo(vo.getAimReqLotNo())
                .setEnableDate(vo.getEnableDate())
                .setDisableDate(vo.getDisableDate());
        return headerEntity;
    }

}
