package com.cvte.scm.wip.app.req.lot;

import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotIssuedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/31 21:19
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class ReqLotIssuedLockApplication {

    private WipReqLotIssuedService wipReqLotIssuedService;

    public ReqLotIssuedLockApplication(WipReqLotIssuedService wipReqLotIssuedService) {
        this.wipReqLotIssuedService = wipReqLotIssuedService;
    }

    public void doAction(String[] idArr) {
        List<String> idList = Arrays.asList(idArr);
        wipReqLotIssuedService.changeLockStatus(idList);
    }

}
