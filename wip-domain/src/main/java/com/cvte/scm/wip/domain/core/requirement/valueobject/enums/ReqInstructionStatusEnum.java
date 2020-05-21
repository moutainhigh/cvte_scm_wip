package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/5/21 12:13
 */
@Getter
@AllArgsConstructor
public enum ReqInstructionStatusEnum implements Codeable {

    UNCONFIRMED("10", "未确认");

    private String code, desc;

}
