package com.cvte.scm.wip.spi.rtc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/30 15:04
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class XxwipTransactionActionDTO {

    private String action;

    private String organizationId;

    private String organizationName;

    private String transactionNumber;

    private String transactionLineId;

    private String rejectReason;

    private String userName;

}
