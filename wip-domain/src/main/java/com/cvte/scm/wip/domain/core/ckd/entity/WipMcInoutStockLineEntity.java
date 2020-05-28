package com.cvte.scm.wip.domain.core.ckd.entity;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import java.util.Date;

/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-05-18
 */
@ApiModel(description = "${table.comment}")
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WipMcInoutStockLineEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String inoutStockLineId;
    /**
     * 配料任务id
     */
    @ApiModelProperty(value = "配料任务id")
    private String inoutStockId;
    /**
     * 配料任务行id
     */
    @ApiModelProperty(value = "配料任务行id")
    private String mcTaskLineId;
    /**
     * 调拨单行id
     */
    @ApiModelProperty(value = "调拨单行id")
    private String lineId;
    /**
     * 调拨单行号
     */
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
    @ApiModelProperty(value = "调拨数量")
    private Integer qty;

    @ApiModelProperty(value = "调拨过账数量")
    private Integer postQty;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;
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
    private Date updTime;
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
