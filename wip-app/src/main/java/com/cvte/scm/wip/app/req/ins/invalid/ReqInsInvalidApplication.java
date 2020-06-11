package com.cvte.scm.wip.app.req.ins.invalid;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.service.CheckReqInsDomainService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/4 17:49
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ReqInsInvalidApplication implements Application<List<ReqInsBuildVO>, String> {

    private DataSourceTransactionManager pgTransactionManager;
    private TransactionTemplate transactionTemplate;
    private CheckReqInsDomainService checkReqInsDomainService;

    public ReqInsInvalidApplication(@Qualifier("pgTransactionManager") DataSourceTransactionManager pgTransactionManager, TransactionTemplate transactionTemplate, CheckReqInsDomainService checkReqInsDomainService) {
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
        this.checkReqInsDomainService = checkReqInsDomainService;
    }

    @Override
    public String doAction(List<ReqInsBuildVO> voList) {
        for (ReqInsBuildVO vo : voList) {
            transactionTemplate.setTransactionManager(pgTransactionManager);
            boolean isProcessed = checkReqInsDomainService.checkInsProcessed(vo);
            if (isProcessed) {
                throw new ServerException(ReqInsErrEnum.INS_IMMUTABLE.getCode(), ReqInsErrEnum.INS_IMMUTABLE.getDesc() + ",指令ID=" + vo.getInsHeaderId());
            }
            transactionTemplate.execute(status -> {
                ReqInsEntity reqIns = ReqInsEntity.get().deleteCompleteReqIns(vo);
                reqIns.notifyEntity();
                return null;
            });
        }
        return null;
    }

}
