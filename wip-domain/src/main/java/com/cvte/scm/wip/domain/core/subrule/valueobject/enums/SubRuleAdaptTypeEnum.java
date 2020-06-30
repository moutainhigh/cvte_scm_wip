package com.cvte.scm.wip.domain.core.subrule.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jf
 * Date    : 2019.02.18
 * Time    : 12:29
 * Email   ：jiangfeng7128@cvte.com
 */
@Getter
@AllArgsConstructor
public enum SubRuleAdaptTypeEnum implements Codeable {

    EQUAL("equal", "等于"),
    INCLUDE("include", "包含"),
    EXCLUDE("exclude", "不包含"),
    UNION("union", "取并集");

    private String code, desc;
}