package com.cvte.scm.wip.domain.core.rtc.valueobject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/19 11:18
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum  WipMtrRtcLotControlTypeEnum {
    REWORK_CONTROL("cn_bill", "更改单批次管控"),
    CONFIG_CONTROL("led", "LED管控"),
    WEAK_CONTROL("inv", "库存批次管控"),
    ;

    private String code, desc;

}
