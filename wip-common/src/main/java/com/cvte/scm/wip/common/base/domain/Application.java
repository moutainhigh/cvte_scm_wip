package com.cvte.scm.wip.common.base.domain;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 12:55
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface Application<T, R> {
    R doAction(T var1);
}
