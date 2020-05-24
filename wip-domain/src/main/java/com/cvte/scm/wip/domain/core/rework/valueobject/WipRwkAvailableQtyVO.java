package com.cvte.scm.wip.domain.core.rework.valueobject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/7 10:00
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipRwkAvailableQtyVO {

    @ApiModelProperty("OCS订单ID")
    private String ocs_order_id;

    /**
     * 组织id_物料ID_工厂ID_批次号_批次状态
     */
    @ApiModelProperty("EBS库存视图主键")
    private String pri_key;

    @ApiModelProperty("OCS订单ID")
    private String order_number;

    @ApiModelProperty("生产批次")
    private String lot_number;

    @ApiModelProperty("此单已预定数量")
    private Long order_reserved_qty;

    @ApiModelProperty("OCS预定数量")
    private Long ocs_reserved_qty;

    @ApiModelProperty("可用数量")
    private Long available_qty;

}
