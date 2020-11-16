package com.cvte.scm.wip.domain.core.rtc.valueobject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/19 11:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum  WipMtrRtcLotControlCodeEnum {
    INACTIVE("1", "未启用批次管控"),
    ACTIVE("2", "启用批次管控")
    ;

    private String code, desc;

}
