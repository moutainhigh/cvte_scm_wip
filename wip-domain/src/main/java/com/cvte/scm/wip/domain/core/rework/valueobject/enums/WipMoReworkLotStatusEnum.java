package com.cvte.scm.wip.domain.core.rework.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/4/2 17:32
 */
@Getter
@AllArgsConstructor
public enum WipMoReworkLotStatusEnum implements Codeable {
    NOT_ONLINE("1", "未上线"),
    ONLINE("3", "已上线"),
    IN_STOCK("100", "库存"),
    ;
    private String code, desc;


    public static WipMoReworkLotStatusEnum getMode(String status) {
        for (WipMoReworkLotStatusEnum statusEnum : values()) {
            if (statusEnum.code.equalsIgnoreCase(status) || statusEnum.desc.equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }
}
