package com.cvte.scm.wip.domain.core.rework.valueobject;

import com.cvte.csb.base.enums.OrderTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/2 17:15
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipRwkMoVO {
    @ApiModelProperty(value = "产品编号")
    private String productNo;

    @ApiModelProperty(value = "客户简称")
    private String consumerName;

    @ApiModelProperty(value = "客户产品编号")
    private String custItemNum;

    @ApiModelProperty(value = "工单批次号")
    private String sourceLotNo;

    @ApiModelProperty(value = "批次状态")
    private String lotStatus;

    @ApiModelProperty(value = "OCS订单ID")
    private String ocsOrderId;

    @ApiModelProperty(value = "工厂ID")
    private String factoryId;

    @ApiModelProperty(value = "产品型号")
    private String productModel;

    @ApiModelProperty(value = "当前页")
    private Integer pageNum;
    @ApiModelProperty("每页记录数")
    private Integer pageSize;
    @ApiModelProperty("排序字段")
    private String orderBy;
    @ApiModelProperty("排序类型")
    private OrderTypeEnum order;
    @ApiModelProperty(value = "是否分页")
    private boolean needPage;

    @ApiModelProperty(value = "发货组织")
    private String shipOrgId;

    @ApiModelProperty(value = "返工类型")
    private String reworkType;

}
