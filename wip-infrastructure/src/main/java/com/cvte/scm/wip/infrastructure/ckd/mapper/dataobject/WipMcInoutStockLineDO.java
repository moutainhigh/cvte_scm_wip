package com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-05-18
 */
@Table(name = "wip_mc_inout_stock_line")
@ApiModel(description = "${table.comment}")
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WipMcInoutStockLineDO extends BaseModel {

    /**
     * ${field.comment}
     */
    @Id
    @Column(name = "inout_stock_line_id")
    @ApiModelProperty(value = "${field.comment}")
    private String inoutStockLineId;
    /**
     * 配料任务id
     */
    @Column(name = "inout_stock_id")
    @ApiModelProperty(value = "配料任务id")
    private String inoutStockId;
    /**
     * 配料任务行id
     */
    @Column(name = "mc_task_line_id")
    @ApiModelProperty(value = "配料任务行id")
    private String mcTaskLineId;
    /**
     * 调拨单行id
     */
    @Column(name = "line_id")
    @ApiModelProperty(value = "调拨单行id")
    private String lineId;
    /**
     * 调拨单行号
     */
    @Column(name = "line_no")
    @ApiModelProperty(value = "调拨单行号")
    private String lineNo;
    /**
     * 状态：UN_POST未过账，POST已过账，CANCEL已取消
     */
    @ApiModelProperty(value = "状态：UN_POST未过账，POST已过账，CANCEL已取消")
    private String status;

    /**
     * 数量
     */
    @Column(name = "qty")
    @ApiModelProperty(value = "调拨数量")
    private Integer qty;

    @Column(name = "post_qty")
    @ApiModelProperty(value = "调拨过账数量")
    private Integer postQty;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;
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
    @Column(name = "upd_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;
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

}
