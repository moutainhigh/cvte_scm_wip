package com.cvte.scm.wip.common.enums;

import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/1/17 15:51
 */

@Getter
public enum StatusEnum {
    /**
     * 正常
     */
    NORMAL("110", "正常"),
    /**
     * 暂挂
     */
    PAUSE("120", "暂挂"),
    /**
     * 关闭
     */
    CLOSE("130", "关闭"),
    /**
     * 作废
     */
    INVALID("140", "作废")
    ;

    private String code;
    private String desc;

    StatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
