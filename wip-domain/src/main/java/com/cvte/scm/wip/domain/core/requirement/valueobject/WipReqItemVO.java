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

    private BigDecimal issuedQty;

    private BigDecimal unIssuedQty;

    private BigDecimal componentYieldFactor;

}
