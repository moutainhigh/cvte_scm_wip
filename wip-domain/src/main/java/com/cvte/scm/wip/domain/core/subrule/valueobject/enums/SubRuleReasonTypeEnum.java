package com.cvte.scm.wip.domain.core.subrule.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jf
 * Date    : 2019.02.13
 * Time    : 15:17
 * Email   ：jiangfeng7128@cvte.com
 */
@Getter
@AllArgsConstructor
public enum SubRuleReasonTypeEnum implements Codeable {

    CONSUME_DULL_MATERIAL("consume_dull_material", "消耗呆料"),
    MATERIAL_BACKUP("material_backup", "物料备份"),
    QUALITY_PROBLEM("quality_problem", "品质问题"),
    DEVELOPMENT_CHANGED("development_changed", "研发变更"),
    BUSINESS_REQUIREMENT("business_requirement", "商务需求"),
    OTHER("other", "其他"),
    CONTACT_LETTER_REPLACE("contact_letter_replace", "联络函替换");

    private String code, desc;
}