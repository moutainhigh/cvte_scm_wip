package com.cvte.scm.wip.domain.core.rework.valueobject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/16 16:49
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ApiReworkBillVO {

    @ApiModelProperty(value = "单据ID")
    private String billId;

    @ApiModelProperty(value = "单据号")
    private String billNo;

    @ApiModelProperty(value = "来源订单号")
    private String sourceOrderId;

    @ApiModelProperty(value = "单据状态")
    private String billStatus;

    @ApiModelProperty(value = "库存组织ID")
    private String organizationId;

    @ApiModelProperty(value = "工厂ID")
    private String factoryId;

    @ApiModelProperty(value = "返工类型")
    private String reworkType;

    @ApiModelProperty(value = "更改原因")
    private String reworkReasonType;

    @ApiModelProperty(value = "更改原因详述")
    private String reworkReason;

    @ApiModelProperty(value = "责任事业部")
    private String respBu;

    @ApiModelProperty(value = "责任部门")
    private String respDept;

    @ApiModelProperty(value = "销售订单号")
    private String sourceSoNo;

    @ApiModelProperty(value = "客户")
    private String consumerName;

    @ApiModelProperty(value = "期望完成时间")
    private Date expectFinishDate;

    @ApiModelProperty(value = "预计出货时间")
    private Date expectDeliveryDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人")
    private String crtUser;

    @ApiModelProperty(value = "创建时间")
    private Date crtDate;

    @ApiModelProperty(value = "更新人")
    private String updUser;

    @ApiModelProperty(value = "更新时间")
    private Date updDate;

    @ApiModelProperty(value = "生产批次")
    private String moLotNo;

    @ApiModelProperty(value = "产品型号")
    private String productModel;

    @ApiModelProperty(value = "批次状态")
    private String moLotStatus;

    @ApiModelProperty(value = "返工数量")
    private String moReworkQty;

}
