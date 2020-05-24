package com.cvte.scm.wip.domain.core.rework.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 返工单行
 *
 * @author author
 * @since 2020-03-23
 */
@Data
@Accessors(chain = true)
public class WipReworkBillLineEntity {

    @ApiModelProperty(value = "单据行ID")
    private String billLineId;

    @ApiModelProperty(value = "返工单ID")
    private String billId;

    @ApiModelProperty(value = "工单ID")
    private String moId;

    @ApiModelProperty(value = "生产批次")
    private String moLotNo;

    @ApiModelProperty(value = "批次状态")
    private String moLotStatus;

    @ApiModelProperty(value = "产品型号")
    private String productModel;

    @ApiModelProperty(value = "返工数量")
    private Long moReworkQty;

    @ApiModelProperty(value = "改前成品编码")
    private String priProductNo;

    @ApiModelProperty(value = "改后成品编码")
    private String subProductNo;

    @ApiModelProperty(value = "数据状态")
    private String status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk01;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk02;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk03;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk04;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk05;

    @ApiModelProperty(value = "创建人")
    private String crtUser;

    @ApiModelProperty(value = "创建时间")
    private Date crtDate;

    @ApiModelProperty(value = "更新人")
    private String updUser;

    @ApiModelProperty(value = "更新时间")
    private Date updDate;

}
