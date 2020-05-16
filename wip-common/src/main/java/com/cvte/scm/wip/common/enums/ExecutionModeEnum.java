package com.cvte.scm.wip.common.enums;

/**
 * 批处理模式枚举，主要针对批处理数据期间，出现错误后的解决模式。
 *
 * @author : jf
 * Date    : 2019.01.07
 * Time    : 11:49
 * Email   ：jiangfeng7128@cvte.com
 */
public enum ExecutionModeEnum {

    /**
     * 严格模式(默认)，如果数据出现错误，则直接抛出异常。
     */
    STRICT,

    /**
     * 懒散模式，如果数据中出现错误，则忽略错误数据，继续执行。
     */
    SLOPPY;

    static ExecutionModeEnum getMode(String mode) {
        for (ExecutionModeEnum modeEnum : values()) {
            if (modeEnum.name().equalsIgnoreCase(mode)) {
                return modeEnum;
            }
        }
        return STRICT;
    }
}