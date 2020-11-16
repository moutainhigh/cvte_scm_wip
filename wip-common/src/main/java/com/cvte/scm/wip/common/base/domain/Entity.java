package com.cvte.scm.wip.common.base.domain;

public interface Entity<T> extends Domain {
    T getUniqueId();
}
