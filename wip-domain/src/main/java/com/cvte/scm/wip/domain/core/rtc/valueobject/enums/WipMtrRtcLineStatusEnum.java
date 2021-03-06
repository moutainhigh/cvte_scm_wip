package com.cvte.scm.wip.domain.core.rtc.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/8 11:20
 */
@Getter
@AllArgsConstructor
public enum WipMtrRtcLineStatusEnum implements Codeable {
    ASSIGNED("20", "未过账"),
    POSTING("30", "过账中"),
    FAILED("35", "过账失败"),
    POSTED("40", "已过账"),
    CLOSED("50", "关闭"),
    CANCELED("60", "取消"),
    ;
    private String code, desc;

    private static final Collection<String> unPostStatus;
    private static final Collection<String> invalidStatus;

    static {
        Set<String> unPostStatusSet = new HashSet<>();
        unPostStatusSet.add(ASSIGNED.code);
        unPostStatusSet.add(FAILED.code);
        unPostStatus = unPostStatusSet;
        Set<String> invalidStatusSet = new HashSet<>();
        invalidStatusSet.add(CLOSED.code);
        invalidStatusSet.add(CANCELED.code);
        invalidStatus = invalidStatusSet;
    }

    // 获取未过账状态集合
    public static Collection<String> getUnPostStatus() {
        return unPostStatus;
    }

    // 获取无效状态集合
    public static Collection<String> getInvalidStatus() {
        return invalidStatus;
    }

}
