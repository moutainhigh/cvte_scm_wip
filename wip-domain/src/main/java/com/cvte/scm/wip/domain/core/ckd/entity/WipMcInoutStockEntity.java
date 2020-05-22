package com.cvte.scm.wip.domain.core.ckd.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-05-18
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcInoutStockEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @Column(name = "inout_stock_id")
    @ApiModelProperty(value = "${field.comment}")
    private String inoutStockId;
    /**
     * 类型：IN 入库，OUT出库
     */
    @ApiModelProperty(value = "类型：IN 入库，OUT出库")
    private String type;
    /**
     * ${field.comment}
     */
    @Column(name = "mc_task_id")
    @ApiModelProperty(value = "${field.comment}")
    private String mcTaskId;
    /**
     * 调拨单头id
     */
    @Column(name = "header_id")
    @ApiModelProperty(value = "调拨单头id")
    private String headerId;
    /**
     * 调拨单号
     */
    @Column(name = "header_no")
    @ApiModelProperty(value = "调拨单号")
    private String headerNo;
    /**
     * 状态：UN_POST未过账，POST已过账，CANCEL已取消
     */
    @ApiModelProperty(value = "状态：UN_POST未过账，POST已过账，CANCEL已取消")
    private String status;
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
