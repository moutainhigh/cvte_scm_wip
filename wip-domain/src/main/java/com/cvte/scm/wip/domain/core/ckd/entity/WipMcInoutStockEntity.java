package com.cvte.scm.wip.domain.core.ckd.entity;


import com.cvte.scm.wip.domain.common.base.BaseModel;
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
public class WipMcInoutStockEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
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
    @ApiModelProperty(value = "${field.comment}")
    private String mcTaskId;
    /**
     * 调拨单头id
     */
    @ApiModelProperty(value = "调拨单头id")
    private String headerId;
    /**
     * 调拨单号
     */
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
