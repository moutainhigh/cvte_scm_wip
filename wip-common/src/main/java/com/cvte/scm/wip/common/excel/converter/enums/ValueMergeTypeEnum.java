package com.cvte.scm.wip.common.excel.converter.enums;

import com.cvte.scm.wip.common.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/25 19:19
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum  ValueMergeTypeEnum implements CodeEnum {
    FIRST("first"),
    SUM("sum"),
    ;

    private String code;

}
