package com.cvte.scm.wip.domain.core.rework.valueobject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/3 16:57
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ApiRwkBillVO {

    @ApiModelProperty("来源订单号")
    private String sourceOrderId;

    @ApiModelProperty("返工单据号")
    private String billNos;
}
