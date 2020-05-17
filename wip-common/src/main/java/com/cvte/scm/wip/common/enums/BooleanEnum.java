package com.cvte.scm.wip.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 布尔枚举
 *
 * @author : jf
 * Date    : 2019.02.12
 * Time    : 15:32
 * Email   ：jiangfeng7128@cvte.com
 */
@Getter
@AllArgsConstructor
public enum BooleanEnum implements Codeable {
    /**
     * 自动模式(默认)，系统自动执行变更操作。
     */
    YES("Y", "是"),

    /**
     * 手工模式，手工执行表更操作。
     */
    NO("N", "否");

    private String code, desc;
}