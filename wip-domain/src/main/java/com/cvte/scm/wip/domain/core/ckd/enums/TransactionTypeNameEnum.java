package com.cvte.scm.wip.domain.core.ckd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用于区分调拨时逻辑，会出现一个ebs字典值对应多个枚举值的情况
 *
 * @author zy
 * @date 2020-05-11 19:03
 **/
@Getter
@AllArgsConstructor
public enum TransactionTypeNameEnum {

    IN("IN", "调拨入库", "厂内调拨"),

    OUT("OUT", "调拨出库", "厂内调拨"),

    RETURN_MATERIAL("RETURN_MATERIAL", "生产退料", "厂内调拨");


    private String code;
    private String optName;
    private String ebsOptName;
}
