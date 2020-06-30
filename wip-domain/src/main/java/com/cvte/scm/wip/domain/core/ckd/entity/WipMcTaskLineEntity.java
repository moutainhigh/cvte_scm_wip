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
 * 配料任务清单表
 *
 * @author zy
 * @since 2020-04-28
 */
@ApiModel(description = "配料任务清单表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcTaskLineEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String lineId;
    /**
     * 配料任务ID
     */
    @ApiModelProperty(value = "配料任务ID")
    private String mcTaskId;
    /**
     * 物料ID
     */
    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 原始单据行号
     */
    @ApiModelProperty(value = "原始单据行号")
    private String sourceLineNo;

    @Column(name = "source_line_id")
    @ApiModelProperty(value = "原始单据行id")
    private String sourceLineId;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer mcQty;


    @ApiModelProperty(value = "行状态")
    private String lineStatus;

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
