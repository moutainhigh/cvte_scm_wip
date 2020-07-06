package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/1 15:39
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class GenerateReqInsDomainService implements DomainService {

    private WipReqHeaderService reqHeaderService;
    private CheckReqInsDomainService checkReqInsDomainService;

    public GenerateReqInsDomainService(WipReqHeaderService reqHeaderService, CheckReqInsDomainService checkReqInsDomainService) {
        this.reqHeaderService = reqHeaderService;
        this.checkReqInsDomainService = checkReqInsDomainService;
    }

    @Transactional("pgTransactionManager")
    public void parseChangeBill(ChangeBillEntity billEntity) {
        // 获取目标投料单
        WipReqHeaderEntity reqHeaderEntity = reqHeaderService.getBySourceId(billEntity.getMoId());
        if (Objects.isNull(reqHeaderEntity)) {
            throw new ServerException(ReqInsErrEnum.TARGET_REQ_INVALID.getCode(), ReqInsErrEnum.TARGET_REQ_INVALID.getDesc());
        }
        ChangeReqVO reqVO = ChangeReqVO.buildVO(reqHeaderEntity);

        // 解析更改单
        ReqInsBuildVO instructionBuildVO = billEntity.parseChangeBill(reqVO);

        // 校验对应指令是否已执行或作废
        boolean insProcessed = checkReqInsDomainService.checkInsProcessed(instructionBuildVO);
        if (insProcessed) {
            throw new ServerException(ReqInsErrEnum.INS_IMMUTABLE.getCode(), String.format("%s,指令ID=%s", ReqInsErrEnum.INS_IMMUTABLE.getDesc(), instructionBuildVO.getInsHeaderId()));
        }
        // 生成投料单指令
        ReqInsEntity.get().completeInstruction(instructionBuildVO);
    }

}
