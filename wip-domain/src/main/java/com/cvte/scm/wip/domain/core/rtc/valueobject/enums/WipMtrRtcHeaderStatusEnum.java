package com.cvte.scm.wip.domain.core.rtc.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/8 11:10
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum WipMtrRtcHeaderStatusEnum implements Codeable {
    DRAFT("10", "草稿"),
    WITHDRAW("15", "退回"),
    REVIEW("20", "审核中"),
    EFFECTIVE("30", "已审核"),
    POSTING("35", "部分过账"),
    COMPLETED("40", "完成"),
    CLOSED("50", "关闭"),
    CANCELED("60", "取消"),
    ;
    private String code, desc;

    private static final Collection<WipMtrRtcHeaderStatusEnum> cancelableStatus;

    static {
        Set<WipMtrRtcHeaderStatusEnum> cancelableStatusSet = new HashSet<>();
        cancelableStatusSet.add(DRAFT);
        cancelableStatusSet.add(REVIEW);
        cancelableStatusSet.add(EFFECTIVE);
        cancelableStatusSet.add(WITHDRAW);
        cancelableStatus = cancelableStatusSet;
    }

    // 获取未过账状态集合
    public static Collection<WipMtrRtcHeaderStatusEnum> getCancelableStatus() {
        return cancelableStatus;
    }

}
