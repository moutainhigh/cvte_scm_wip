package com.cvte.scm.wip.domain.core.rework.entity;


import com.cvte.csb.validator.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 返工单
 *
 * @author author
 * @since 2020-03-23
 */
@Data
@Accessors(chain = true)
public class WipReworkBillHeaderEntity {

    @ApiModelProperty(value = "单据ID")
    private String billId;

    @ApiModelProperty(value = "单据号")
    private String billNo;

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

    @ApiModelProperty(value = "更改要求")
    private String reworkDesc;

    @ApiModelProperty(value = "责任事业部")
    private String respBu;

    @ApiModelProperty(value = "责任部门")
    private String respDept;

    @ApiModelProperty(value = "期望完成时间")
    private Date expectFinishDate;

    @ApiModelProperty(value = "预计出货时间")
    private Date expectDeliveryDate;

    @ApiModelProperty(value = "销售订单号")
    private String sourceSoNo;

    @ApiModelProperty(value = "客户")
    private String consumerName;

    @ApiModelProperty(value = "不良品处理方式")
    private String rejectDealType;

    @ApiModelProperty(value = "良品处理方式")
    private String goodDealType;

    @ApiModelProperty(value = "不良品物料处理方式")
    private String rejectMtrDealType;

    @ApiModelProperty(value = "良品物料处理方式")
    private String goodMtrDealType;

    @ApiModelProperty(value = "其他处理方式")
    private String rejectDealOtherType;

    private String remark;

    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    private String rmk06;

    private String rmk07;

    private String rmk08;

    private String rmk09;

    private String rmk10;

    @ApiModelProperty(value = "创建人")
    private String crtUser;

    @ApiModelProperty(value = "创建时间")
    private Date crtDate;

    @ApiModelProperty(value = "更新人")
    private String updUser;

    @ApiModelProperty(value = "更新时间")
    private Date updDate;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "来源系统")
    private String sourceCode;

}
