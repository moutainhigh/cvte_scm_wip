package com.cvte.scm.wip.domain.core.subrule.valueobject;

import lombok.Data;

/**
 * @author : jf
 * Date    : 2020.03.05
 * Time    : 12:34
 * Email   ：jiangfeng7128@cvte.com
 */
@Data
public class WipSubRuleItemDetailVO {
    /* 组织ID */
    private String organizationId;
    /* 物料ID */
    private String itemId;
    /* 物料编码 */
    private String itemNo;
    /* 物料状态 */
    private String itemStatus;
    /* 物料详情 */
    private String itemSpec;
    /* 物料可用量 */
    private String onhandQty;
}