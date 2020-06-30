package com.cvte.scm.wip.domain.core.subrule.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jf
 * Date    : 2019.02.13
 * Time    : 15:04
 * Email   ：jiangfeng7128@cvte.com
 */
@Getter
@AllArgsConstructor
public enum SubRuleStatusEnum implements Codeable {

    DRAFT("draft", "草稿"),
    REVIEW("review", "审核中"),
    EFFECTIVE("effective", "生效中"),
    INVALID("invalid", "已作废"),
    EXPIRED("expired", "已失效");

    private String code, desc;
}