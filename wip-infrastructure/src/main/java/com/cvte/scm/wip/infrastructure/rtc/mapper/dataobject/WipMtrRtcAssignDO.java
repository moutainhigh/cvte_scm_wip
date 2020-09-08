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
 * 领退料单分配
 *
 * @author xueyuting
 * @since 2020-09-08
 */
@Table(name = "wip_mtr_rtc_assign")
@ApiModel(description = "领退料单分配")
@Data
@EqualsAndHashCode(callSuper = false)
public class WipMtrRtcAssignDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 分配ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "assign_id")
    @ApiModelProperty(value = "分配ID")
    private String assignId;
    /**
     * 组织ID
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织ID")
    private String organizationId;
    /**
     * 单据Id
     */
    @Column(name = "header_id")
    @ApiModelProperty(value = "单据Id")
    private String headerId;
    /**
     * 单据行Id
     */
    @Column(name = "line_id")
    @ApiModelProperty(value = "单据行Id")
    private String lineId;
    /**
     * 物料批次号
     */
    @Column(name = "mtr_lot_no")
    @ApiModelProperty(value = "物料批次号")
    private String mtrLotNo;
    /**
     * 工厂ID
     */
    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂ID")
    private String factoryId;
    /**
     * 分配数量
     */
    @Column(name = "assign_qty")
    @ApiModelProperty(value = "分配数量")
    private BigDecimal assignQty;
    /**
     * 子库
     */
    @Column(name = "invp_no")
    @ApiModelProperty(value = "子库")
    private String invpNo;
    /**
     * 源单据ID
     */
    @Column(name = "source_order_id")
    @ApiModelProperty(value = "源单据ID")
    private String sourceOrderId;
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
