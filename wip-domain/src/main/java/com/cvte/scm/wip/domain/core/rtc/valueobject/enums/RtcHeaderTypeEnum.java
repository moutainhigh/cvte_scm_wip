package com.cvte.scm.wip.domain.core.rtc.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/8 12:10
 */
@Getter
@AllArgsConstructor
public enum RtcHeaderTypeEnum implements Codeable {
    RECEIVE("rec", "领料"),
    RETURN("rtn", "退料"),
    ;
    private String code, desc;
}
