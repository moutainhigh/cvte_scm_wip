package com.cvte.scm.wip.domain.core.subrule.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jf
 * Date    : 2019.02.13
 * Time    : 15:10
 * Email   ：jiangfeng7128@cvte.com
 */
@Getter
@AllArgsConstructor
public enum SubRuleScopeTypeEnum implements Codeable {

    BOM("BOM", "BOM"),
    DEVELOPMENT_MODEL("development_model", "研发型号"),
    PRODUCTION_LOT("production_lot", "生产批次"),
    ORDER_CUSTOMER("order_customer", "下单客户");

    private String code, desc;
}