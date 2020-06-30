package com.cvte.scm.wip.common.event;

import com.cvte.scm.wip.common.base.domain.DomainEvent;
import com.cvte.scm.wip.common.base.domain.EventListener;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * SimpleEventBus
 * date 2019/4/6
 */
@Component
public class SimpleEventBus {

    private final EventBus eventBus = new EventBus("tenancy");

    @Autowired
    private List<EventListener> listeners;

    public void register(EventListener listener) {
        eventBus.register(listener);
    }

    public void fire(DomainEvent event) {
        eventBus.post(event);
    }

    /**
     * 暂时未实现
     */
    public void asyncFire(DomainEvent event) {
        throw new RuntimeException("异步eventBus暂时不支持");
    }

    @PostConstruct
    public void init() {
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }
        listeners.forEach(eventBus::register);
    }

}
