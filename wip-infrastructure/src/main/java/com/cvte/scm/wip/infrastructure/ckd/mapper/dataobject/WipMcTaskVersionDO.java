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
 * 配料任务版本表
 *
 * @author zy
 * @since 2020-04-28
 */
@Table(name = "wip_mc_task_version")
@ApiModel(description = "配料任务版本表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcTaskVersionDO extends BaseModel {
    /**
     * ${field.comment}
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "version_id")
    @ApiModelProperty(value = "${field.comment}")
    private String versionId;
    /**
     * ${field.comment}
     */
    @Column(name = "version_no")
    @ApiModelProperty(value = "${field.comment}")
    private String versionNo;

    @Column(name = "version_date")
    private Date versionDate;
    /**
     * ${field.comment}
     */
    @Column(name = "mc_task_id")
    @ApiModelProperty(value = "${field.comment}")
    private String mcTaskId;
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
