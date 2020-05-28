package com.cvte.scm.wip.domain.core.requirement.entity;


import com.cvte.scm.wip.common.constants.CommonConstant;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 整机螺丝类物料工艺属性基表
 *
 * @author zy
 * @since 2020-05-22
 */
@ApiModel(description = "整机螺丝类物料工艺属性基表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipItemWkpPosEntity extends BaseModel {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 物料code
     */
    @ApiModelProperty(value = "物料code")
    private String itemCode;

    @ApiModelProperty(value = "组织id")
    private String organizationId;

    @ApiModelProperty(value = "生效时间")
    private Date beginDate;

    @ApiModelProperty(value = "失效时间")
    private Date endDate;

    private String productModel;
    /**
     * 位置a
     */
    @ApiModelProperty(value = "位置a")
    private String locationA;
    /**
     * 位置b
     */
    @ApiModelProperty(value = "位置b")
    private String locationB;
    /**
     * 物料单位用量
     */
    @ApiModelProperty(value = "物料单位用量")
    private Integer unitQty;
    /**
     * 位置a部件数量
     */
    @ApiModelProperty(value = "位置a部件数量")
    private Integer locationAQty;
    /**
     * 工艺属性
     */
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

    /**
     * 获取唯一索引键
     *
     * @return java.lang.String
     **/
    public String generateUniqueKey() {
        return this.getOrganizationId()
                + CommonConstant.COMMON_SEPARATOR
                + this.getProductModel()
                + CommonConstant.COMMON_SEPARATOR
                + this.getItemCode()
                + CommonConstant.COMMON_SEPARATOR
                + this.getTechniqueAttr();
    }

}
