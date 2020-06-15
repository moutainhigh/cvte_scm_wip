package com.cvte.scm.wip.common.base.domain;

import org.springframework.stereotype.Component;

@Component
public interface EventListener<T extends Event> {
    void execute(T var1);
}
