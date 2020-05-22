package com.cvte.scm.wip.domain.core.changebill.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/5/21 15:43
 */
@Getter
@AllArgsConstructor
public enum ChangeBillStatusEnum implements Codeable {
    ACTIVE("active", "生效"),
    UNDO("undo", "撤销"),
    ;

    private String code, desc;

}
