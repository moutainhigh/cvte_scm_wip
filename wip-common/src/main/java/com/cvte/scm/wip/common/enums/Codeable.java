package com.cvte.scm.wip.common.enums;

/**
 * 用于支持具有{@param code}字段的枚举
 *
 * @author : jf
 * Date    : 2020.01.01
 * Time    : 10:11
 * Email   ：jiangfeng7128@cvte.com
 */
public interface Codeable {
    Object getCode();

    String getDesc();
}