package com.cvte.scm.wip.domain.core.rework.valueobject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 11:39
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class WipRwkBillLVO {

    @ApiModelProperty(value = "单据行ID")
    private String billLineId;

    @ApiModelProperty(value = "单据ID")
    private String billId;

    @ApiModelProperty(value = "工厂ID")
    private String factoryId;

    @ApiModelProperty(value = "生产批次")
    private String moLotNo;

    @ApiModelProperty(value = "批次状态")
    private String moLotStatus;

    @ApiModelProperty(value = "产品型号")
    private String productModel;

    @ApiModelProperty(value = "返工数量")
    private Long moReworkQty;

    @ApiModelProperty(value = "产品代码")
    private String productNo;

    @ApiModelProperty(value = "改前物料")
    private String priProductNo;

    @ApiModelProperty(value = "改后物料")
    private String subProductNo;

    @ApiModelProperty(value = "批次备注")
    private String remark;

}
