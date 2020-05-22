package com.cvte.scm.wip.domain.thirdpart.thirdpart.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy
 * @date 2020-05-14 16:12
 **/
@Getter
@AllArgsConstructor
public enum EbsDeliveryStatusEnum {

    BOOKED("BOOKED", "已确认"),

    CANCELLED("CANCELLED", "取消"),

    ENTER("ENTER", "录入"),

    POSTED("POSTED", "已过账");

    private String code;

    private String name;
}
