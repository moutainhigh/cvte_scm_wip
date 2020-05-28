package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 整机螺丝类物料工艺属性基表
 *
 * @author zy
 * @since 2020-05-22
 */
@Table(name = "wip_item_wkp_pos")
@ApiModel(description = "整机螺丝类物料工艺属性基表")
@Data
@EqualsAndHashCode
public class WipItemWkpPosDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 物料id
     */
    @Column(name = "item_code")
    @ApiModelProperty(value = "物料id")
    private String itemCode;

    @ApiModelProperty(value = "组织id")
    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "begin_date")
    @ApiModelProperty(value = "生效时间")
    private Date beginDate;

    @Column(name = "end_date")
    @ApiModelProperty(value = "失效时间")
    private Date endDate;

    @Column(name = "product_model")
    private String productModel;

    /**
     * 位置a
     */
    @Column(name = "location_a")
    @ApiModelProperty(value = "位置a")
    private String locationA;
    /**
     * 位置b
     */
    @Column(name = "location_b")
    @ApiModelProperty(value = "位置b")
    private String locationB;
    /**
     * 物料单位用量
     */
    @Column(name = "unit_qty")
    @ApiModelProperty(value = "物料单位用量")
    private Integer unitQty;
    /**
     * 位置a部件数量
     */
    @Column(name = "location_a_qty")
    @ApiModelProperty(value = "位置a部件数量")
    private Integer locationAQty;
    /**
     * 工艺属性
     */
    @Column(name = "technique_attr")
    @ApiModelProperty(value = "工艺属性")
    private String techniqueAttr;
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
