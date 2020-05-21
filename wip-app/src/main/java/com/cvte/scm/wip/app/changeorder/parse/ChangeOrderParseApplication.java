package com.cvte.scm.wip.app.changeorder.parse;

import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.domain.core.changeorder.entity.ChangeOrderEntity;
import com.cvte.scm.wip.domain.core.changeorder.service.SourceChangeOrderService;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderBuildVO;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqHeaderService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 17:11
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ChangeOrderParseApplication implements Application<String, String> {

    private SourceChangeOrderService sourceChangeOrderService;
    private WipReqHeaderService reqHeaderService;

    public ChangeOrderParseApplication(SourceChangeOrderService sourceChangeOrderService, WipReqHeaderService reqHeaderService) {
        this.sourceChangeOrderService = sourceChangeOrderService;
        this.reqHeaderService = reqHeaderService;
    }

    @Override
    @Transactional("pgTransactionManager")
    public String doAction(String var1) {

        // 获取EBS更改单
        List<ChangeOrderBuildVO> changeOrderBuildVOList = sourceChangeOrderService.querySourceChangeOrder(null);

        for (ChangeOrderBuildVO changeOrderBuildVO : changeOrderBuildVOList) {
            // 生成更改单
            ChangeOrderEntity orderEntity = ChangeOrderEntity.get().creatCompleteChangeOrder(changeOrderBuildVO);

            // 获取目标投料单
            WipReqHeaderEntity reqHeaderEntity = reqHeaderService.getBySourceId(orderEntity.getMoId());
            ChangeReqVO reqVO = ChangeReqVO.buildVO(reqHeaderEntity);

            // 解析更改单
            ReqInstructionBuildVO instructionBuildVO = orderEntity.parseChangeOrder(reqVO);

            // 生成投料单指令
            ReqInstructionEntity.get().createCompleteInstruction(instructionBuildVO);
        }

        return null;
    }

}
