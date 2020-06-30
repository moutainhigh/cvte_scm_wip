package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/2 10:58
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum InsOperationTypeEnum implements Codeable {
    DELETE("1", "删除"),
    ADD("2", "新增"),
    REPLACE("3", "替换"),
    REDUCE("4", "减少"),
    INCREASE("5", "增加"),
    ;

    private String code, desc;
}
