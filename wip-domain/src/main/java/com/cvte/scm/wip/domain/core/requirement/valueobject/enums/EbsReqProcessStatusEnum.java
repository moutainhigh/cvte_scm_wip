package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/3/12 16:46
 */
@Getter
@AllArgsConstructor
public enum EbsReqProcessStatusEnum implements Codeable {
    SUCCESS("success", "成功"),
    FAILED("failed", "失败"),
    NO_CHANGE("no change", "无变更"),
    ;
    private String code, desc;
}
