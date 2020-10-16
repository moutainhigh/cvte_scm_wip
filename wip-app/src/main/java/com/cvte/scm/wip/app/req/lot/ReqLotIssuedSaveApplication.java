package com.cvte.scm.wip.app.req.lot;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotIssuedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/28 10:30
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class ReqLotIssuedSaveApplication {

    private WipReqLotIssuedService wipReqLotIssuedService;

    public ReqLotIssuedSaveApplication(WipReqLotIssuedService wipReqLotIssuedService) {
        this.wipReqLotIssuedService = wipReqLotIssuedService;
    }

    @Transactional(transactionManager = "pgTransactionManager")
    public void doAction(List<WipReqLotIssuedEntity> wipReqLotIssuedList) {
        // 新增领料
        wipReqLotIssuedService.saveAll(wipReqLotIssuedList);
    }

}
