package com.cvte.scm.wip.domain.core.changebill.event;

import com.cvte.scm.wip.common.base.domain.DomainEvent;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import lombok.Data;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/11 17:39
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class ChangeBillParseErrorEvent implements DomainEvent {

    private ChangeBillEntity changeBillEntity;

    private String errorMessage;

    public ChangeBillParseErrorEvent(ChangeBillEntity changeBillEntity, String errorMessage) {
        this.changeBillEntity = changeBillEntity;
        this.errorMessage = errorMessage;
    }
}
