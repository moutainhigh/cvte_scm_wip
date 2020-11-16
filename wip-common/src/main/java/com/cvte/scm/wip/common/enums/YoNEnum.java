package com.cvte.scm.wip.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/8/31 21:10
 */
@Getter
@AllArgsConstructor
public enum YoNEnum implements Codeable{

    Y("Y", "是"),
    N("N", "否"),
    ;

    private String code, desc;

}
