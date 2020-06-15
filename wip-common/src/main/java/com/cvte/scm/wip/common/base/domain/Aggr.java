package com.cvte.scm.wip.common.base.domain;

import com.cvte.scm.wip.common.base.domain.Domain;

public interface Aggr<T> extends Domain {
    T getAggrRoot();
}

