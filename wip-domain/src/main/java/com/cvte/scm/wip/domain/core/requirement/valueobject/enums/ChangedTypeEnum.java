package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jf
 * Date    : 2019.01.06
 * Time    : 09:41
 * Email   ：jiangfeng7128@cvte.com
 */
@Getter
@AllArgsConstructor
public enum ChangedTypeEnum implements Codeable {
    ADD("add", "新增"),
    DELETE("delete", "删除"),
    UPDATE("update", "更新"),
    REPLACE("replace", "替换"),
    PREPARE("prepare", "备料"),
    ISSUED_ADD("issued_add", "添加领料"),
    ISSUED_UPDATE("issued_update", "更新领料"),
    ISSUED_INVALID("issued_invalid", "失效领料"),
    EXECUTE("execute", "自动改投料"),
    REDUCE("reduce", "减少"),
    INCREASE("increase", "增加"),
    WKP_REPLACE("wkp_replace", "工序替换");

    private String code, desc;
}
