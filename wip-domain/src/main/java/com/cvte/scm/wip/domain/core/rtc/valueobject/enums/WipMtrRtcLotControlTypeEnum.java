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
    REWORK_CONTROL("1", "返工管控"),
    CONFIG_CONTROL("2", "配置强管控"),
    WEAK_CONTROL("3", "弱管控"),
    ;

    private String code, desc;

}
