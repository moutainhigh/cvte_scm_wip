package com.cvte.scm.wip.domain.core.rtc.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 12:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrSubInvVO {

    private String organizationId;

    private String factoryId;

    private String factoryNo;

    private String inventoryItemId;

    private String lotNumber;

    private Date firstStockinDate;

    private String subinventoryCode;

    private BigDecimal supplyQty;

}
