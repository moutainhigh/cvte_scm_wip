package com.cvte.scm.wip.app.req.lot;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotProcessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 17:13
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class ReqLotProcessApplication {

    private WipReqLotProcessService wipReqLotProcessService;

    public ReqLotProcessApplication(WipReqLotProcessService wipReqLotProcessService) {
        this.wipReqLotProcessService = wipReqLotProcessService;
    }

    public String doAction(List<WipReqLotProcessEntity> wipReqLotProcessList) {
        wipReqLotProcessService.createAndProcess(wipReqLotProcessList);
        return "";
    }

}
