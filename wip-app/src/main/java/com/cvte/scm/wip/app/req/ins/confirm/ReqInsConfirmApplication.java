package com.cvte.scm.wip.app.req.ins.confirm;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.service.CheckReqInsDomainService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLineService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/2 11:02
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ReqInsConfirmApplication implements Application<String[], String> {

    private CheckReqInsDomainService checkReqInsDomainService;
    private WipReqLineService wipReqLineService;
    private DataSourceTransactionManager pgTransactionManager;
    private TransactionTemplate transactionTemplate;

    public ReqInsConfirmApplication(CheckReqInsDomainService checkReqInsDomainService, WipReqLineService wipReqLineService
            , @Qualifier("pgTransactionManager")DataSourceTransactionManager pgTransactionManager
            , TransactionTemplate transactionTemplate) {
        this.checkReqInsDomainService = checkReqInsDomainService;
        this.wipReqLineService = wipReqLineService;
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public String doAction(String[] insHeaderIds) {

        List<ReqInsEntity> insHeaderList = new ArrayList<>();
        for (String insHeaderId : insHeaderIds) {
            ReqInsEntity insHeader = ReqInsEntity.get().getByKey(insHeaderId);
            if (Objects.isNull(insHeader)) {
                throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc() + String.format("ID为%s的指令不存在", insHeaderId));
            }
            insHeader.getDetailById();
            insHeaderList.add(insHeader);
        }
        insHeaderList.sort((Comparator.comparing(ReqInsEntity::getEnableDate)));

        for (ReqInsEntity insHeader : insHeaderList) {

            Map<String, List<WipReqLineEntity>> reqLineMap;
            try {
                checkReqInsDomainService.checkPreInsExists(insHeader);
                reqLineMap = checkReqInsDomainService.validAndGetLine(insHeader);
                checkReqInsDomainService.checkLineStatus(insHeader, reqLineMap);
                checkReqInsDomainService.checkPartMix(insHeader, reqLineMap);
            } catch (RuntimeException re) {
                if (!ProcessingStatusEnum.SOLVED.getCode().equals(insHeader.getStatus())) {
                    insHeader.processFailed("校验失败," + re.getMessage());
                }
                throw re;
            }

            List<WipReqLineEntity> reqLineList = insHeader.parse(reqLineMap);

            transactionTemplate.setTransactionManager(pgTransactionManager);
            try {
                transactionTemplate.execute(status -> {
                    wipReqLineService.executeByChangeBill(reqLineList, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());

                    insHeader.processSuccess();
                    return null;
                });
            } catch (RuntimeException re) {
                insHeader.processFailed("执行异常," + re.getMessage());
                throw re;
            }

        }

        return null;
    }
}
