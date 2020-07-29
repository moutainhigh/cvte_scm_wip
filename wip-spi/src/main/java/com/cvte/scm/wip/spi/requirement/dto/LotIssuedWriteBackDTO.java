package com.cvte.scm.wip.spi.requirement.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/28 10:13
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class LotIssuedWriteBackDTO {

    private String headerId;

    private String lotNo;

    private String wkpNo;

    private String itemId;

    private Integer lotQty;

    private String opType;

}
