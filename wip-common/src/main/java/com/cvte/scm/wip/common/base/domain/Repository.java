package com.cvte.scm.wip.common.base.domain;

import com.cvte.scm.wip.common.base.ApplicationContextHelper;

@org.springframework.stereotype.Repository
public interface Repository {
    static <T> T get(Class<T> clazz) {
        return ApplicationContextHelper.getBean(clazz);
    }
}