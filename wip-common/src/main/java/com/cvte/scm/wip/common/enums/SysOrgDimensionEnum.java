package com.cvte.scm.wip.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/4/2 18:39
 */
@Getter
@AllArgsConstructor
public enum SysOrgDimensionEnum {
    GYL("GYL", "供应链"),
    GC("GC", "工厂")
    ;
    private String code, desc;
}
