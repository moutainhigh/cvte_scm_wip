package com.cvte.scm.wip.app.req.line;

import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInsEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.service.CheckReqInsDomainService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLineService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/28 10:09
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class ReqLineModifyQtyApplication {

    private ReqInsEntityFactory reqInsEntityFactory;
    private CheckReqInsDomainService checkReqInsDomainService;
    private WipReqLineService wipReqLineService;
    private DataSourceTransactionManager pgTransactionManager;
    private TransactionTemplate transactionTemplate;

    public ReqLineModifyQtyApplication(ReqInsEntityFactory reqInsEntityFactory, CheckReqInsDomainService checkReqInsDomainService
            , WipReqLineService wipReqLineService, @Qualifier("pgTransactionManager") DataSourceTransactionManager pgTransactionManager
            , TransactionTemplate transactionTemplate) {
        this.reqInsEntityFactory = reqInsEntityFactory;
        this.checkReqInsDomainService = checkReqInsDomainService;
        this.wipReqLineService = wipReqLineService;
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
    }

    public void doAction(List<WipReqLineEntity> changeLineList) {

        ReqInsEntity insHeader = reqInsEntityFactory.perfect(changeLineList);

        Map<String, List<WipReqLineEntity>> reqLineMap = checkReqInsDomainService.validAndGetLine(insHeader);
        checkReqInsDomainService.checkLineStatus(insHeader);

        List<WipReqLineEntity> reqLineList = insHeader.parse(reqLineMap);

        transactionTemplate.setTransactionManager(pgTransactionManager);
        transactionTemplate.execute(status -> {
            wipReqLineService.executeByChangeBill(reqLineList, ChangedTypeEnum.CHANGE, ExecutionModeEnum.STRICT, ChangedModeEnum.MANUAL, true, EntityUtils.getWipUserId());
            return null;
        });

    }

}
