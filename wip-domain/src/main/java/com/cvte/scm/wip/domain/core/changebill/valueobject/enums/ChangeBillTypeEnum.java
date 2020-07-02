package com.cvte.scm.wip.domain.core.changebill.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/7/2 18:37
 */
@Getter
@AllArgsConstructor
public enum ChangeBillTypeEnum implements Codeable {
    RD("rd", "研发更改单"),
    PUR("pur", "采购更改单"),
    FACTORY_CHANGE("factory_change", "工厂手工变更"),
    ;

    private String code, desc;

}
