package com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 领退料单
 *
 * @author xueyuting
 * @since 2020-09-08
 */
@Table(name = "wip_mtr_rtc_h")
@ApiModel(description = "领退料单")
@Data
@EqualsAndHashCode(callSuper = false)
public class WipMtrRtcHDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 头ID
     */
    @Id
    @Column(name = "header_id")
    @ApiModelProperty(value = "头ID")
    private String headerId;
    /**
     * 组织ID
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织ID")
    private String organizationId;
    /**
     * 单据编号
     */
    @Column(name = "bill_no")
    @ApiModelProperty(value = "单据编号")
    private String billNo;
    /**
     * 单据类型
     */
    @Column(name = "bill_type")
    @ApiModelProperty(value = "单据类型")
    private String billType;
    /**
     * 工单ID
     */
    @Column(name = "mo_id")
    @ApiModelProperty(value = "工单ID")
    private String moId;
    /**
     * 工序
     */
    @Column(name = "wkp_no")
    @ApiModelProperty(value = "工序")
    private String wkpNo;
    /**
     * 部门
     */
    @Column(name = "dept_no")
    @ApiModelProperty(value = "部门")
    private String deptNo;
    /**
     * 工厂
     */
    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂")
    private String factoryId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 领料套数
     */
    @Column(name = "bill_qty")
    @ApiModelProperty(value = "领料套数")
    private BigDecimal billQty;
    /**
     * 子库
     */
    @Column(name = "invp_no")
    @ApiModelProperty(value = "子库")
    private String invpNo;
    /**
     * 单据状态
     */
    @Column(name = "bill_status")
    @ApiModelProperty(value = "单据状态")
    private String billStatus;
    /**
     * 来源单据号
     */
    @Column(name = "source_bill_no")
    @ApiModelProperty(value = "来源单据号")
    private String sourceBillNo;
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
    /**
     * 审核人
     */
    @Column(name = "reviewer")
    @ApiModelProperty(value = "审核人")
    private String reviewer;
    /**
     * 审核时间
     */
    @Column(name = "review_time")
    @ApiModelProperty(value = "审核时间")
    private Date reviewTime;

}
