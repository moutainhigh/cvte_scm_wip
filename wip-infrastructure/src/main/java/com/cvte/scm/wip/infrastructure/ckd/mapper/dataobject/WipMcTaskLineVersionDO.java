package com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject;


import com.cvte.scm.wip.domain.common.base.BaseModel;
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
 * 配料任务清单版本表
 *
 * @author zy
 * @since 2020-04-28
 */
@Table(name = "wip_mc_task_line_version")
@ApiModel(description = "配料任务清单版本表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcTaskLineVersionDO extends BaseModel {

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
    @Column(name = "version_id")
    @ApiModelProperty(value = "版本id")
    private String versionId;

    /**
     * 物料id
     */
    @Column(name = "item_id")
    @ApiModelProperty(value = "物料id")
    private String itemId;
    /**
     * 销售订单行号
     */
    @Column(name = "so_line_no")
    @ApiModelProperty(value = "销售订单行号")
    private String soLineNo;
    /**
     * 销售订单行号
     */
    @Column(name = "so_line_id")
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
    @Column(name = "delivery_out_no")
    @ApiModelProperty(value = "调拨出库单号")
    private String deliveryOutNo;
    /**
     * 调拨出库数量
     */
    @Column(name = "delivery_out_qty")
    @ApiModelProperty(value = "调拨出库数量")
    private Integer deliveryOutQty;
    /**
     * 调拨出库过账数量
     */
    @Column(name = "delivery_out_post_qty")
    @ApiModelProperty(value = "调拨出库过账数量")
    private Integer deliveryOutPostQty;
    /**
     * 调拨入库单号
     */
    @Column(name = "delivery_in_no")
    @ApiModelProperty(value = "调拨入库单号")
    private String deliveryInNo;
    /**
     * 调拨入库数量
     */
    @Column(name = "delivery_in_qty")
    @ApiModelProperty(value = "调拨入库数量")
    private Integer deliveryInQty;
    /**
     * 调拨入库过账数量
     */
    @Column(name = "delivery_in_post_qty")
    @ApiModelProperty(value = "调拨入库过账数量")
    private Integer deliveryInPostQty;
    @Column(name = "line_status")
    @ApiModelProperty(value = "行状态")
    private String lineStatus;

    @Column(name = "delivery_in_status")
    private String deliveryInStatus;

    @Column(name = "delivery_out_status")
    private String deliveryOutStatus;

    @Column(name = "delivery_in_line_no")
    private String deliveryInLineNo;

    @Column(name = "delivery_out_line_no")
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
    @Column(name = "crt_user")
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_host")
    @ApiModelProperty(value = "${field.comment}")
    private String crtHost;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_user")
    @ApiModelProperty(value = "${field.comment}")
    private String updUser;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_host")
    @ApiModelProperty(value = "${field.comment}")
    private String updHost;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;

}
