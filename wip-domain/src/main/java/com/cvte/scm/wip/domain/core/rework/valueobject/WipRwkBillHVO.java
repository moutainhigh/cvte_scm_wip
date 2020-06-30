package com.cvte.scm.wip.domain.core.rework.valueobject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 11:39
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class WipRwkBillHVO {

    @ApiModelProperty(value = "单据ID")
    private String billId;

    @ApiModelProperty(value = "单据号")
    private String billNo;

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

    @ApiModelProperty(value = "期望完成时间")
    private Date expectFinishDate;

    @ApiModelProperty(value = "预计出货时间")
    private Date expectDeliveryDate;

    @ApiModelProperty(value = "单据状态")
    private String billStatus;

    @ApiModelProperty(value = "客户")
    private String consumerName;

    @ApiModelProperty(value = "销售订单号")
    private String sourceSoNo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "来源系统")
    private String sourceCode;

    @ApiModelProperty(value = "单据行列表")
    private List<WipRwkBillLVO> billLines;

}
