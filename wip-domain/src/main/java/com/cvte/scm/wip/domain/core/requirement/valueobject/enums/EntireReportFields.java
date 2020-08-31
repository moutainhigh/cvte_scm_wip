package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/26 11:44
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum EntireReportFields {
    ITEM_NO("item_no", "物料编码"),
    WKP_NO("wkp_no", "工序"),
    ITEM_DESC("item_desc", "物料描述"),
    CRAFT_ATTR("craft_attr", "工艺属性"),
    CRAFT_DESC("craft_desc", "工艺描述"),
    CRAFT_REQ("craft_req", "工艺要求"),
    ITEM_CLASS("item_class", "物料分类"),
    REPLACE_GROUP("replace_group", "替代组"),
    INV_QTY("inv_qty", "即时库存"),
    IS_FACTORY_PUR("is_factory_pur", "是否工厂代采"),
    REQ_QTY("req_qty", "合计需求"),
    ;

    private String field, desc;

}
