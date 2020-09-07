package com.cvte.scm.wip.app.changebill.sync;

import com.cvte.scm.wip.app.changebill.parse.ChangeBillParseApplication;
import com.cvte.scm.wip.app.req.line.ReqLineSyncApplication;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/1 20:05
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ChangeBillFullSyncApplication {

    private ChangeBillParseApplication changeBillParseApplication;
    private ReqLineSyncApplication reqLineSyncApplication;

    public ChangeBillFullSyncApplication(ChangeBillParseApplication changeBillParseApplication, ReqLineSyncApplication reqLineSyncApplication) {
        this.changeBillParseApplication = changeBillParseApplication;
        this.reqLineSyncApplication = reqLineSyncApplication;
    }

    public void doAction(ChangeBillQueryVO queryVO) {
        Map<String, Object> map = new HashMap<>();
        map.put("organizationId", queryVO.getOrganizationId());
        map.put("factoryId", queryVO.getFactoryId());
        reqLineSyncApplication.doAction(map);

        changeBillParseApplication.doAction(queryVO);
    }

}
