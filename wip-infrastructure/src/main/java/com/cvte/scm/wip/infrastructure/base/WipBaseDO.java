package com.cvte.scm.wip.infrastructure.base;

/**
 * @author zy
 * @date 2020-05-21 14:43
 **/
public abstract class WipBaseDO<M extends WipBaseDO, T> {

    public final WipBaseDO EMPTY_OBJ = build();


    abstract WipBaseDO build();

}
