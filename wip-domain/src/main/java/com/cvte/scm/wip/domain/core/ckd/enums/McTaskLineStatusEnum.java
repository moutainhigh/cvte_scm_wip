package com.cvte.scm.wip.domain.core.ckd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy
 * @date 2020-04-30 09:29
 **/
@Getter
@AllArgsConstructor
public enum McTaskLineStatusEnum {

    NORMAL("110", "正常"),

    ABANDON("130", "作废");

    private String code;

    private String value;

}
