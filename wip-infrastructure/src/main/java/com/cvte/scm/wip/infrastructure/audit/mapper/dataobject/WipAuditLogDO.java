package com.cvte.scm.wip.infrastructure.audit.mapper.dataobject;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 操作日志
 *
 * @author author
 * @since 2020-10-10
 */
@Table(name = "wip_audit_log")
@ApiModel(description = "操作日志")
@Data
@EqualsAndHashCode(callSuper = false)
public class WipAuditLogDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * 操作批次
     */
    @Column(name = "batch_id")
    @ApiModelProperty(value = "操作批次")
    private String batchId;
    /**
     * 模块
     */
    @ApiModelProperty(value = "模块")
    private String model;
    /**
     * 操作实体
     */
    @ApiModelProperty(value = "操作实体")
    private String entity;
    /**
     * 操作对象ID
     */
    @Column(name = "object_id")
    @ApiModelProperty(value = "操作对象ID")
    private String objectId;
    /**
     * 操作对象的父级ID
     */
    @Column(name = "object_parent_id")
    @ApiModelProperty(value = "操作对象的父级ID")
    private String objectParentId;
    /**
     * 操作类型
     */
    @Column(name = "operation_type")
    @ApiModelProperty(value = "操作类型")
    private String operationType;
    /**
     * 操作字段
     */
    @ApiModelProperty(value = "操作字段")
    private String field;
    /**
     * 操作字段名称
     */
    @Column(name = "field_name")
    @ApiModelProperty(value = "操作字段名称")
    private String fieldName;
    /**
     * 改前
     */
    @ApiModelProperty(value = "改前")
    private String before;
    /**
     * 改后
     */
    @ApiModelProperty(value = "改后")
    private String after;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk01;
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
    @Column(name = "upd_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;

}
