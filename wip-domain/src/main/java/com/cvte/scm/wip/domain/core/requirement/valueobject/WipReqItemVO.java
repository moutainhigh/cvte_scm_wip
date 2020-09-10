package com.cvte.scm.wip.domain.core.requirement.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/8 15:57
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipReqItemVO {

    private String organizationId;

    private String moId;

    private String itemId;

    private String itemNo;

    private String wkpNo;

    private BigDecimal unitQty;

    private BigDecimal reqQty;

    // 已发料
    private BigDecimal issuedQty;

    private BigDecimal unIssuedQty;

    // BOM产出率
    private BigDecimal componentYieldFactor;

    // 已申请未过账的领/退料数量
    private BigDecimal unPostQty;

    public String getKey() {
        return String.join("_", this.organizationId, this.moId, this.itemId, this.wkpNo);
    }

}
