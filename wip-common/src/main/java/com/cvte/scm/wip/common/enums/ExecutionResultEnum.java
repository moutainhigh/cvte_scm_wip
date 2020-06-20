package com.cvte.scm.wip.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/6/19 11:02
 */
@Getter
@AllArgsConstructor
public enum ExecutionResultEnum implements Codeable {
    SUCCESS("1", "成功"),
    FAILED("2", "失败"),
    SKIP("3", "跳过"),
    ;

    private String code, desc;

}
