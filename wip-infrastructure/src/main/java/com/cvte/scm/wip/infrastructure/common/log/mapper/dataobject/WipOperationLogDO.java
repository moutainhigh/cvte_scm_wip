package com.cvte.scm.wip.infrastructure.common.log.mapper.dataobject;


import com.cvte.csb.validator.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-05-22
 */
@Table(name = "wip_operation_log")
@ApiModel(description = "${table.comment}")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipOperationLogDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * 模块
     */
    @ApiModelProperty(value = "模块")
    private String module;
    /**
     * 关联id
     */
    @Column(name = "reference_id")
    @ApiModelProperty(value = "关联id")
    private String referenceId;
    /**
     * 具体操作
     */
    @ApiModelProperty(value = "具体操作")
    private String operation;
    /**
     * 日志详情
     */
    @ApiModelProperty(value = "日志详情")
    private String detail;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;
    /**
     * 用户名
     */
    @Column(name = "crt_name")
    @ApiModelProperty(value = "用户名")
    private String crtName;
    /**
     * 操作时间
     */
    @Column(name = "crt_time")
    @ApiModelProperty(value = "操作时间")
    private Date crtTime;

}
