package com.cvte.scm.wip.domain.core.rtc.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/8 11:20
 */
@Getter
@AllArgsConstructor
public enum RtcLineStatusEnum implements Codeable {
    DRAFT("10", "未分配"),
    ASSIGNED("20", "已分配"),
    POSTING("30", "过账中"),
    FAILED("35", "过账失败"),
    POSTED("40", "已过账"),
    ;
    private String code, desc;
}
