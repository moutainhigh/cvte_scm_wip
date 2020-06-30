package com.cvte.scm.wip.domain.core.ckd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy
 * @date 2020-04-30 14:42
 **/
@Getter
@AllArgsConstructor
public enum McTaskVersionChangTypeEnum {

    INSERT("INSERT", "新增"),

    DELETE("DELETE", "删除"),

    INCREMENT("INCREMENT", "数量增加"),

    DECREMENT("DECREMENT", "数量减少");

    private String code;

    private String value;
}
