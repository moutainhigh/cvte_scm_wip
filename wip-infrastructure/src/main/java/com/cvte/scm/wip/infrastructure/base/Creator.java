package com.cvte.scm.wip.infrastructure.base;

import org.apache.poi.ss.formula.functions.T;

/**
 * @author zy
 * @date 2020-05-21 15:00
 **/
public class Creator extends GenericWithCreate<T> {

    @Override
    public T create() {
        return new T();
    }
}
