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
 * 配料任务清单表
 *
 * @author zy
 * @since 2020-04-28
 */
@Table(name = "wip_mc_task_line")
@ApiModel(description = "配料任务清单表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcTaskLineDO extends BaseModel {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "line_id")
    @ApiModelProperty(value = "主键")
    private String lineId;
    /**
     * 配料任务ID
     */
    @Column(name = "mc_task_id")
    @ApiModelProperty(value = "配料任务ID")
    private String mcTaskId;
    /**
     * 物料ID
     */
    @Column(name = "item_id")
    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 原始单据行号
     */
    @Column(name = "source_line_no")
    @ApiModelProperty(value = "原始单据行号")
    private String sourceLineNo;

    @Column(name = "source_line_id")
    @ApiModelProperty(value = "原始单据行id")
    private String sourceLineId;
    /**
     * 数量
     */
    @Column(name = "mc_qty")
    @ApiModelProperty(value = "数量")
    private Integer mcQty;


    @Column(name = "line_status")
    @ApiModelProperty(value = "行状态")
    private String lineStatus;

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
