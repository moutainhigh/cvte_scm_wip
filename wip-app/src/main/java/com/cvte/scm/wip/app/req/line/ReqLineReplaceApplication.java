package com.cvte.scm.wip.app.req.line;

import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.service.GenerateChangeBillDomainService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLineService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/1 15:28
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class ReqLineReplaceApplication implements Application<List<WipReqLineEntity>, String> {

    private GenerateChangeBillDomainService generateChangeBillDomainService;
    private WipReqLineService wipReqLineService;

    public ReqLineReplaceApplication(GenerateChangeBillDomainService generateChangeBillDomainService, WipReqLineService wipReqLineService) {
        this.generateChangeBillDomainService = generateChangeBillDomainService;
        this.wipReqLineService = wipReqLineService;
    }

    @Override
    @Transactional(transactionManager = "pgTransactionManager")
    public String doAction(List<WipReqLineEntity> wipReqLinesList) {

        ChangeReqVO reqVO = new ChangeReqVO();
        ChangeBillBuildVO changeBillBuildVO = generateChangeBillDomainService.generate(wipReqLinesList, reqVO);

        // 生成更改单
        ChangeBillEntity billEntity = ChangeBillEntity.get().completeChangeBill(changeBillBuildVO);

        // 解析更改单
        ReqInsBuildVO instructionBuildVO = billEntity.parseChangeBill(reqVO);
        // 生成投料指令
        ReqInsEntity insEntity = ReqInsEntity.get().completeInstruction(instructionBuildVO);
        // 指令直接更新为 已处理
        insEntity.processSuccess();

        wipReqLineService.replace(wipReqLinesList, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());

        return null;
    }
}
