package com.cvte.scm.wip.app.changebill.parse;

import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.service.SourceChangeBillService;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
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
public class ChangeBillParseApplication implements Application<ChangeBillQueryVO, String> {

    private SourceChangeBillService sourceChangeBillService;
    private WipReqHeaderService reqHeaderService;

    public ChangeBillParseApplication(SourceChangeBillService sourceChangeBillService, WipReqHeaderService reqHeaderService) {
        this.sourceChangeBillService = sourceChangeBillService;
        this.reqHeaderService = reqHeaderService;
    }

    @Override
    @Transactional("pgTransactionManager")
    public String doAction(ChangeBillQueryVO queryVO) {

        // 获取EBS更改单
        List<ChangeBillBuildVO> changeBillBuildVOList = sourceChangeBillService.querySourceChangeBill(queryVO);

        for (ChangeBillBuildVO changeBillBuildVO : changeBillBuildVOList) {
            // 生成更改单
            ChangeBillEntity billEntity = ChangeBillEntity.get().completeChangeBill(changeBillBuildVO);

            // 获取目标投料单
            WipReqHeaderEntity reqHeaderEntity = reqHeaderService.getBySourceId(billEntity.getMoId());
            ChangeReqVO reqVO = ChangeReqVO.buildVO(reqHeaderEntity);

            // 解析更改单
            ReqInstructionBuildVO instructionBuildVO = billEntity.parseChangeBill(reqVO);

            // 生成投料单指令
            ReqInstructionEntity.get().completeInstruction(instructionBuildVO);
        }

        return null;
    }

}
