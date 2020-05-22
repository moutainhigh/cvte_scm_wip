package com.cvte.scm.wip.infrastructure.base;

/**
 * @author zy
 * @date 2020-05-21 14:55
 **/
public abstract class WipBaseEntity<M extends WipBaseEntity> {

    public abstract WipBaseEntity build();
}
