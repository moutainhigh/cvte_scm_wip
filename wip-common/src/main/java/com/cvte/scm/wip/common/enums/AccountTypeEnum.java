package com.cvte.scm.wip.common.enums;

import lombok.Getter;

/**
 * sys_user表账户类型枚举
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/2/22 10:40
 */
@Getter
public enum AccountTypeEnum implements Codeable {
    /**
     * 内部账户
     */
    INNER("2", "内部账户"),
    /**
     * 外部账户
     */
    OUTER("1", "外部账户");

    private String code, desc;

    AccountTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
