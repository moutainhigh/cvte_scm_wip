package com.cvte.scm.wip.infrastructure.requirement.message;

import com.cvte.scm.wip.common.base.domain.EventListener;
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
    @Subscribe
    @Override
    public void execute(ReqInsProcessNotifyEvent event) {
        ReqInsEntity reqIns = event.getReqInsEntity();
        log.info("ins process notify test, id={}", reqIns.getInsHeaderId());
    }
}
