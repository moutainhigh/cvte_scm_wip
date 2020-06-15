package com.cvte.scm.wip.domain.core.changebill.valueobject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/6/11 18:15
 */
@Getter
@AllArgsConstructor
public enum ChangeBillOptTypeEnum {
    SYNC("1", "同步"),
    EXECUTE("2", "执行"),
    ;

    private String code, desc;

}
