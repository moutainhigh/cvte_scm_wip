package com.cvte.scm.wip.infrastructure.base;

/**
 * @author zy
 * @date 2020-05-21 15:00
 **/
public abstract class GenericWithCreate<T> {

    final T element;

    GenericWithCreate() {
        element = create();
    }

    //create()交由具体要实例化的子类实现
    public abstract T create();
}
