package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/27 14:48
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum  LotIssuedLockTypeEnum {
    NONE("none", "无锁定"),
    AUTO("auto", "自动锁定"),
    MANUAL("manual", "手工锁定"),
    ;

    private String code, desc;

}
