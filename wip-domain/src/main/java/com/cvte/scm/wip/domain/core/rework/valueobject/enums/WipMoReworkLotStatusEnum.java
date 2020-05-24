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
    IN_STOCK("1", "库存"),
    ONLINE("2", "上线"),
    NOT_ONLINE("3", "未上线"),
    ANOTHER("100", "其他"),
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
