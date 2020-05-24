package com.cvte.scm.wip.domain.core.rework.valueobject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/28 10:15
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class MoLotVO {
    @ApiModelProperty(value = "库存组织ID")
    private String organizationId;

    @ApiModelProperty(value = "库存组织名称")
    private String organizationName;

    @ApiModelProperty(value = "工厂ID")
    private String factoryId;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "生产批次号")
    private String moLotNo;

    @ApiModelProperty(value = "客户简称")
    private String consumerName;

    @ApiModelProperty(value = "产品代码")
    private String productNo;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品型号")
    private String productModel;

    @ApiModelProperty(value = "内勤")
    private String omName;

    @ApiModelProperty(value = "客户料号")
    private String custItemNum;

    @ApiModelProperty(value = "此单已预定数量")
    private Long orderReservedQty;

    @ApiModelProperty(value = "OCS预定数量")
    private Long ocsReservedQty;

    @ApiModelProperty(value = "可用数量")
    private Long availableQty;

    @ApiModelProperty(value = "在库天数")
    private Long availableDays;

    @ApiModelProperty(value = "可用库存数量")
    private Long invStockQty;

    @ApiModelProperty(value = "即时库存")
    private Long onhandQty;

    @ApiModelProperty(value = "累计库存更改单未领料数量")
    private Long cnQtyRequired;

    @ApiModelProperty(value = "理论库存")
    private Long reStockQty;

    @ApiModelProperty(value = "累计唛头或标贴库存返工数量")
    private Long totalRwkQty;

    @ApiModelProperty(value = "批次状态")
    private String moLotStatus;

    @ApiModelProperty(value = "返工数量")
    private Long moReworkQty;

    @ApiModelProperty(value = "批次备注")
    private String remark;
}
