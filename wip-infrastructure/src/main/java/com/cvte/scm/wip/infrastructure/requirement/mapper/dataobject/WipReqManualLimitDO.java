package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import com.cvte.csb.validator.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 手工变更投料限制
 *
 * @author author
 * @since 2020-07-17
 */
@Table(name = "wip_req_manual_limit")
@ApiModel(description = "手工变更投料限制")
@Data
@EqualsAndHashCode(callSuper = false)
public class WipReqManualLimitDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * 组织
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织")
    private String organizationId;
    /**
     * 物料
     */
    @Column(name = "item_class")
    @ApiModelProperty(value = "物料")
    private String itemClass;
    /**
     * 变更类型
     */
    @Column(name = "change_type")
    @ApiModelProperty(value = "变更类型")
    private String changeType;
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

    @Column(name = "role_code")
    @ApiModelProperty(value = "角色编码")
    private String roleCode;

}
