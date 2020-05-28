package com.cvte.scm.wip.domain.core.ckd.entity;


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
 * 配料任务单与需求单关系表
 *
 * @author zy
 * @since 2020-04-28
 */
@Table(name = "wip_mc_task")
@ApiModel(description = "配料任务单与需求单关系表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcTaskEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "mc_task_id")
    @ApiModelProperty(value = "主键")
    private String mcTaskId;
    /**
     * 配料任务编号
     */
    @Column(name = "mc_task_no")
    @ApiModelProperty(value = "配料任务编号")
    private String mcTaskNo;

    @Column(name = "mc_wf_id")
    @ApiModelProperty(value = "配料任务编号")
    private String mcWfId;

    /**
     * 工厂
     */
    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂")
    private String factoryId;
    /**
     * 客户
     */
    @ApiModelProperty(value = "客户")
    private String client;
    /**
     * 齐套日期
     */
    @Column(name = "mtr_ready_time")
    @ApiModelProperty(value = "齐套日期")
    private Date mtrReadyTime;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "back_office")
    @ApiModelProperty(value = "内勤")
    private String backOffice;

    @Column(name = "bu_name")
    @ApiModelProperty(value = "事业部")
    private String buName;

    @Column(name = "dept_name")
    @ApiModelProperty(value = "部门")
    private String deptName;

    @Column(name = "org_id")
    @ApiModelProperty(value = "scm库存组织")
    private String orgId;

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
