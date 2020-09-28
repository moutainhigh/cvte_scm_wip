package com.cvte.scm.wip.app.req.lot;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.service.LotIssuedWriteBackService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotIssuedService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedOpTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/28 10:34
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class ReqLotIssuedDeleteApplication {

    private WipReqLotIssuedService wipReqLotIssuedService;
    private LotIssuedWriteBackService lotIssuedWriteBackService;

    private DataSourceTransactionManager pgTransactionManager;
    private TransactionTemplate transactionTemplate;

    public ReqLotIssuedDeleteApplication(WipReqLotIssuedService wipReqLotIssuedService
            , LotIssuedWriteBackService lotIssuedWriteBackService, @Qualifier("pgTransactionManager") DataSourceTransactionManager pgTransactionManager
            , TransactionTemplate transactionTemplate) {
        this.wipReqLotIssuedService = wipReqLotIssuedService;
        this.lotIssuedWriteBackService = lotIssuedWriteBackService;
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
    }

    public void doAction(String idListStr) {

        List<String> idList = Arrays.asList(idListStr.split(","));

        transactionTemplate.setTransactionManager(pgTransactionManager);
        for (String id : idList) {
            transactionTemplate.execute(status -> {

                // 作废领料
                wipReqLotIssuedService.invalid(id);
                // 回写EBS
                WipReqLotIssuedEntity invalidedEntity = wipReqLotIssuedService.selectById(id);
                lotIssuedWriteBackService.writeBack(LotIssuedOpTypeEnum.REMOVE, invalidedEntity);
                log.info("失效领料批次成功,idList = {}", id);

                return null;
            });
        }
    }

}
