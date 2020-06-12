package com.cvte.scm.wip.domain.core.ckd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy
 * @date 2020-05-12 16:05
 **/
@Getter
@AllArgsConstructor
public enum McTaskDeliveryStatusEnum {

    UN_POST("UN_POST", "未过账"),

    POSTED("POSTED", "已过账"),

    CANCELLED("CANCELLED", "已取消");

    private String code;

    private String value;
}
