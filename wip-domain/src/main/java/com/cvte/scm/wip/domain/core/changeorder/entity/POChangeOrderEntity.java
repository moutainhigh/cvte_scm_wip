package com.cvte.scm.wip.domain.core.changeorder.entity;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.changeorder.repository.ChangeOrderRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionDetailBuildVO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 15:49
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class POChangeOrderEntity extends ChangeOrderEntity {

    public POChangeOrderEntity(ChangeOrderRepository changeOrderRepository) {
        super(changeOrderRepository);
    }

    public ReqInstructionBuildVO parseChangeOrder(List<ChangeOrderDetailEntity> orderDetailEntityList) {
        ReqInstructionBuildVO instructionBuildVO = new ReqInstructionBuildVO();
        instructionBuildVO.setInstructionHeaderId(UUIDUtils.get32UUID())
                .setAimHeaderId(this.getBillId());

        List<ReqInstructionDetailBuildVO> instructionDetailBuildVOList = new ArrayList<>();
        for (ChangeOrderDetailEntity orderDetailEntity : orderDetailEntityList) {
            ReqInstructionDetailBuildVO instructionDetailBuildVO = new ReqInstructionDetailBuildVO();
            instructionDetailBuildVO.setInstructionDetailId(UUIDUtils.get32UUID())
                    .setSourceChangeDetailId(orderDetailEntity.getDetailId());
            instructionDetailBuildVOList.add(instructionDetailBuildVO);
        }
        instructionBuildVO.setInstructionDetailList(instructionDetailBuildVOList);
        return instructionBuildVO;
    }

    public static POChangeOrderEntity get() {
        return DomainFactory.get(POChangeOrderEntity.class);
    }

}
