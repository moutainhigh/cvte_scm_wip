package com.cvte.scm.wip.domain.core.ckd.entity;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 配料任务清单版本表
 *
 * @author zy
 * @since 2020-04-28
 */
@ApiModel(description = "配料任务清单版本表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcTaskLineVersionEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * 版本id
     */
    @ApiModelProperty(value = "版本id")
    private String versionId;

    /**
     * 物料id
     */
    @ApiModelProperty(value = "物料id")
    private String itemId;
    /**
     * 销售订单行号
     */
    @ApiModelProperty(value = "销售订单行号")
    private String soLineNo;
    /**
     * 销售订单行号
     */
    @ApiModelProperty(value = "销售订单行号")
    private String soLineId;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer qty;
    /**
     * 调拨出库单号
     */
    @ApiModelProperty(value = "调拨出库单号")
    private String deliveryOutNo;
    /**
     * 调拨出库数量
     */
    @ApiModelProperty(value = "调拨出库数量")
    private Integer deliveryOutQty;
    /**
     * 调拨出库过账数量
     */
    @ApiModelProperty(value = "调拨出库过账数量")
    private Integer deliveryOutPostQty;
    /**
     * 调拨入库单号
     */
    @ApiModelProperty(value = "调拨入库单号")
    private String deliveryInNo;
    /**
     * 调拨入库数量
     */
    @ApiModelProperty(value = "调拨入库数量")
    private Integer deliveryInQty;
    /**
     * 调拨入库过账数量
     */
    @ApiModelProperty(value = "调拨入库过账数量")
    private Integer deliveryInPostQty;

    @ApiModelProperty(value = "行状态")
    private String lineStatus;

    private String deliveryInStatus;

    private String deliveryInLineStatus;

    private String deliveryOutStatus;

    private String deliveryOutLineStatus;

    private String deliveryInLineNo;

    private String deliveryOutLineNo;

    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk02;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk03;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk04;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk05;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String crtHost;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String updUser;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String updHost;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;

}
