package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/7/28 10:01
 */
@Getter
@AllArgsConstructor
public enum LotIssuedOpTypeEnum implements Codeable {
    ADD("add", "新增"),
    REMOVE("remove", "删除"),
    UPDATE("update", "更新"),
    ;

    private String code, desc;
}
