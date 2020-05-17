package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

/**
 * 变更模式枚举
 *
 * @author : jf
 * Date    : 2019.02.10
 * Time    : 11:20
 * Email   ：jiangfeng7128@cvte.com
 */
public enum ChangedModeEnum {

    /**
     * 自动模式(默认)，系统自动执行变更操作。
     */
    AUTOMATIC,

    /**
     * 手工模式，手工执行表更操作。
     */
    MANUAL;

    static ChangedModeEnum getMode(String mode) {
        for (ChangedModeEnum modeEnum : values()) {
            if (modeEnum.name().equalsIgnoreCase(mode)) {
                return modeEnum;
            }
        }
        return AUTOMATIC;
    }
}