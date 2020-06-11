package com.cvte.scm.wip.infrastructure.requirement.message;

import com.cvte.csb.core.exception.client.forbiddens.NoOperationRightException;
import com.cvte.scm.wip.common.base.domain.EventListener;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.service.ChangeBillWriteBackService;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.event.ReqInsProcessNotifyEvent;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/11 11:47
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ReqInsProcessNotifyListener implements EventListener<ReqInsProcessNotifyEvent> {

    private ChangeBillWriteBackService changeBillWriteBackService;

    public ReqInsProcessNotifyListener(ChangeBillWriteBackService changeBillWriteBackService) {
        this.changeBillWriteBackService = changeBillWriteBackService;
    }

    @Subscribe
    @Override
    public void execute(ReqInsProcessNotifyEvent event) {
        ReqInsEntity reqIns = event.getReqInsEntity();
        try {
            EntityUtils.retry(() -> changeBillWriteBackService.writeBackToEbs(reqIns), 3, "回写EBS更改单");
        } catch (NoOperationRightException ne) {
            String message = ne.getMessage();
            reqIns.setExecuteResult(message);
            reqIns.updateInstruction();
        }
    }
}
