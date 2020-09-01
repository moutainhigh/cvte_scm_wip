package com.cvte.scm.wip.app.req.lot;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import com.cvte.scm.wip.domain.core.requirement.factory.WipReqLotPrecessEntityFactor;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotProcessDomainService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLotProcessVO;
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

    private WipReqLotPrecessEntityFactor wipReqLotPrecessEntityFactor;
    private WipReqLotProcessDomainService processDomainService;

    public ReqLotProcessApplication(WipReqLotPrecessEntityFactor wipReqLotPrecessEntityFactor, WipReqLotProcessDomainService processDomainService) {
        this.wipReqLotPrecessEntityFactor = wipReqLotPrecessEntityFactor;
        this.processDomainService = processDomainService;
    }

    public String doAction(List<WipReqLotProcessVO> voList) {
        for (WipReqLotProcessVO vo : voList) {
            vo.validate();
            WipReqLotProcessEntity lotProcessEntity = wipReqLotPrecessEntityFactor.perfect(vo);
            lotProcessEntity.create(vo);

            processDomainService.process(lotProcessEntity);
        }

        return "";
    }

}
