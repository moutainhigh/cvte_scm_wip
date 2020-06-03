package com.cvte.scm.wip.domain.core.ckd.enums;

import com.cvte.scm.wip.common.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy
 * @date 2020-06-03 11:29
 **/
@Getter
@AllArgsConstructor
public enum McTaskFinishStatusEnums implements CodeEnum {

    FINISH("FINISH", "已完成"),

    UN_FINISH("UN_FINISH", "未完成");

    private String code;

    private String value;
}
