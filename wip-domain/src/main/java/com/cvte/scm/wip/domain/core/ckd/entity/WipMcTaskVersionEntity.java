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
 * 配料任务版本表
 *
 * @author zy
 * @since 2020-04-28
 */
@ApiModel(description = "配料任务版本表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcTaskVersionEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String versionId;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String versionNo;

    private Date versionDate;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String mcTaskId;
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
    @ApiModelProperty(value = "${field.comment}")
    private String updHost;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;

}
