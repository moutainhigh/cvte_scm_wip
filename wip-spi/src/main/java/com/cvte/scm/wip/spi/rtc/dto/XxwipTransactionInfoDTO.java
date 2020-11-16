package com.cvte.scm.wip.spi.rtc.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/30 15:34
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class XxwipTransactionInfoDTO {

    @ApiModelProperty("单据ID")
    private String transactionHeaderId;

    @ApiModelProperty("单据号")
    private String transactionNumber;

    @ApiModelProperty("单据状态")
    private String transactionStatus;


    @ApiModelProperty("单据行ID")
    private String transactionLineId;

    @ApiModelProperty("单据行物料ID")
    private String componentItemId;

    @ApiModelProperty("单据行物料编号")
    private String componentItem;

    @ApiModelProperty("工序")
    private String operationSeqNum;

    @ApiModelProperty("单据行状态")
    private String lineStatusCode;

}
