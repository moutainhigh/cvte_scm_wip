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

    public static InsOperationTypeEnum getOpposite(InsOperationTypeEnum operationTypeEnum) {
        switch (operationTypeEnum) {
            case ADD:
                return InsOperationTypeEnum.DELETE;
            case DELETE:
                return InsOperationTypeEnum.ADD;
            case INCREASE:
                return InsOperationTypeEnum.REDUCE;
            case REDUCE:
                return InsOperationTypeEnum.INCREASE;
            default:
                return operationTypeEnum;
        }
    }

    private String code, desc;
}
