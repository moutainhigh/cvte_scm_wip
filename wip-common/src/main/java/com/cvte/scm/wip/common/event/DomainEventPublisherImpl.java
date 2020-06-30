package com.cvte.scm.wip.common.event;

import com.cvte.scm.wip.common.base.domain.DomainEvent;
import com.cvte.scm.wip.common.base.domain.DomainEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息发布者
 * 默认都是同步的
 * date 2019/4/6
 */
@Component
public class DomainEventPublisherImpl implements DomainEventPublisher {

    @Resource
    private SimpleEventBus simpleEventBus;

    @Override
    public void publish(DomainEvent event, Boolean async) {
        if (!async) {
            simpleEventBus.fire(event);
            return;
        }
        simpleEventBus.asyncFire(event);
    }

}
