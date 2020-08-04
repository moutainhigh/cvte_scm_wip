package com.cvte.scm.wip.domain.core.ckd.dto.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zy
 * @date 2020-04-30 09:51
 **/
@Data
@Accessors(chain = true)
public class WipMcTaskLineView {

    @ApiModelProperty(value = "主键")
    private String lineId;

    @ApiModelProperty(value = "配料任务ID")
    private String mcTaskId;

    @ApiModelProperty(value = "物料ID")
    private String itemId;

    private String itemCode;

    @ApiModelProperty(value = "原始单据行号")
    private String sourceLineNo;

    @ApiModelProperty(value = "原始单据行id")
    private String sourceLineId;

    @ApiModelProperty(value = "数量")
    private Integer mcQty;

    private String deliveryOutLineNo;

    @ApiModelProperty(value = "调拨出库单号")
    private String deliveryOutNo;

    @ApiModelProperty(value = "调拨出库状态")
    private String deliveryOutStatus;

    @ApiModelProperty(value = "调拨出库行状态")
    private String deliveryOutLineStatus;

    @ApiModelProperty(value = "调拨出库数量")
    private Integer deliveryOutQty;

    @ApiModelProperty(value = "调拨出库过账数量")
    private Integer deliveryOutPostQty;

    private String deliveryInLineNo;

    @ApiModelProperty(value = "调拨入库单号")
    private String deliveryInNo;

    @ApiModelProperty(value = "调拨入库状态")
    private String deliveryInStatus;

    @ApiModelProperty(value = "调拨入库行状态")
    private String deliveryInLineStatus;

    @ApiModelProperty(value = "调拨入库数量")
    private Integer deliveryInQty;

    @ApiModelProperty(value = "调拨入库过账数量")
    private Integer deliveryInPostQty;

    private String deliveryInLineSource;


    private String deliveryRmLineNo;

    @ApiModelProperty(value = "退料单号")
    private String deliveryRmNo;

    @ApiModelProperty(value = "退料单状态")
    private String deliveryRmStatus;

    @ApiModelProperty(value = "退料行状态")
    private String deliveryRmLineStatus;

    @ApiModelProperty(value = "退料数量")
    private Integer deliveryRmQty;

    @ApiModelProperty(value = "退料过账数量")
    private Integer deliveryRmPostQty;

    private String deliveryRmLineSource;

    @ApiModelProperty(value = "行状态")
    private String lineStatus;

    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;

    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;

    @ApiModelProperty(value = "${field.comment}")
    private String crtHost;

    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;

    @ApiModelProperty(value = "${field.comment}")
    private String updUser;

    @ApiModelProperty(value = "${field.comment}")
    private String updHost;

    @ApiModelProperty(value = "工厂编码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂id")
    private String factoryId;

    @ApiModelProperty(value = "工厂名")
    private String factoryName;

    private String deliveryOutStockLineId;

    private String deliveryInStockLineId;

}
