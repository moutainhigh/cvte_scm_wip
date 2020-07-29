package com.cvte.scm.wip.app.req.issue;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.service.LotIssuedWriteBackService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotIssuedService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedOpTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private LotIssuedWriteBackService lotIssuedWriteBackService;

    public ReqLotIssuedSaveApplication(WipReqLotIssuedService wipReqLotIssuedService, LotIssuedWriteBackService lotIssuedWriteBackService) {
        this.wipReqLotIssuedService = wipReqLotIssuedService;
        this.lotIssuedWriteBackService = lotIssuedWriteBackService;
    }

    @Transactional(transactionManager = "pgTransactionManager")
    public void doAction(WipReqLotIssuedEntity wipReqLotIssued) {
        // 新增领料
        wipReqLotIssuedService.add(wipReqLotIssued);
        // 写入日志
        wipReqLotIssuedService.generateIssuedLog(wipReqLotIssued, wipReqLotIssued, ChangedTypeEnum.ISSUED_ADD.getCode());
        // 回写EBS
        lotIssuedWriteBackService.writeBack(LotIssuedOpTypeEnum.ADD, wipReqLotIssued);
        log.info("新增领料批次成功,id = {}", wipReqLotIssued.getId());
    }

}
