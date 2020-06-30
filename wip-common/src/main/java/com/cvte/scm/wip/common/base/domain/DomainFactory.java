package com.cvte.scm.wip.common.base.domain;

import com.cvte.scm.wip.common.base.ApplicationContextHelper;

public interface DomainFactory<V extends VO, D extends Domain> {
    D perfect(V vo);

    static <T> T get(Class<T> clazz) {
        return ApplicationContextHelper.getBean(clazz);
    }
}
