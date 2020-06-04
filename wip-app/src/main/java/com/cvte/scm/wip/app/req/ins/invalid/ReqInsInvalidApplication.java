package com.cvte.scm.wip.app.req.ins.invalid;

import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
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

    public ReqInsInvalidApplication(@Qualifier("pgTransactionManager") DataSourceTransactionManager pgTransactionManager, TransactionTemplate transactionTemplate) {
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public String doAction(List<ReqInsBuildVO> voList) {
        for (ReqInsBuildVO vo : voList) {
            transactionTemplate.setTransactionManager(pgTransactionManager);
            transactionTemplate.execute(status -> {
                ReqInsEntity.get().deleteCompleteReqIns(vo);
                return null;
            });
        }
        return null;
    }

}
