package com.cvte.scm.wip.common.enums;

import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/2/24 16:33
 */
@Getter
public enum AuditorNodeEnum implements Codeable {
    CURRENT_AUDITOR("currentAuditor", "当前审核人"),
    DRAFT_NODE("draftNode", "起草节点")
    ;

    private String code, desc;

    AuditorNodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
