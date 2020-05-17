package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jf
 * Date    : 2019.12.31
 * Time    : 09:15
 * Email   ：jiangfeng7128@cvte.com
 */
@Getter
@AllArgsConstructor
public enum BillStatusEnum implements Codeable {

    DRAFT("10", "草稿"),
    CONFIRMED("20", "已确定"),
    PREPARED("30", "已备料"),
    ISSUED("35", "已领料"),
    CLOSED("40", "已关闭"),
    CANCELLED("50", "已取消");

    private String code, desc;
}