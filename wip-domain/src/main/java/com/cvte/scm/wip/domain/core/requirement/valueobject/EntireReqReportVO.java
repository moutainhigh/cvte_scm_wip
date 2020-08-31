package com.cvte.scm.wip.domain.core.requirement.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/21 16:21
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class EntireReqReportVO {

    private String sourceLotNo;

    private String itemNo;

    private String itemId;

    private String itemDesc;

    private String itemClass;

    private BigDecimal reqQty;

}
