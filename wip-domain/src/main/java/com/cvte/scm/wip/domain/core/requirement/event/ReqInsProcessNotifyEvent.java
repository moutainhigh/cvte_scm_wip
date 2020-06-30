package com.cvte.scm.wip.domain.core.requirement.event;

import com.cvte.scm.wip.common.base.domain.DomainEvent;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import lombok.Data;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/11 11:44
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class ReqInsProcessNotifyEvent implements DomainEvent {

    private ReqInsEntity reqInsEntity;

    public ReqInsProcessNotifyEvent(ReqInsEntity reqInsEntity) {
        this.reqInsEntity = reqInsEntity;
    }

}
