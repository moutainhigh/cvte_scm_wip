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
 * 领退料单行
 *
 * @author xueyuting
 * @since 2020-09-08
 */
@Table(name = "wip_mtr_rtc_l")
@ApiModel(description = "领退料单行")
@Data
@EqualsAndHashCode(callSuper = false)
public class WipMtrRtcLDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 单据行ID
     */
    @Id
    @Column(name = "line_id")
    @ApiModelProperty(value = "单据行ID")
    private String lineId;
    /**
     * 单据ID
     */
    @Column(name = "header_id")
    @ApiModelProperty(value = "单据ID")
    private String headerId;
    /**
     * 组织ID
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织ID")
    private String organizationId;
    /**
     * 物料ID
     */
    @Column(name = "item_id")
    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 物料编码
     */
    @Column(name = "item_no")
    @ApiModelProperty(value = "物料编码")
    private String itemNo;
    /**
     * 生产批次号(预留：小批次)
     */
    @Column(name = "mo_lot_no")
    @ApiModelProperty(value = "生产批次号(预留：小批次)")
    private String moLotNo;
    /**
     * 工序
     */
    @Column(name = "wkp_no")
    @ApiModelProperty(value = "工序")
    private String wkpNo;
    /**
     * 子库
     */
    @Column(name = "invp_no")
    @ApiModelProperty(value = "子库")
    private String invpNo;
    /**
     * 需求数量
     */
    @Column(name = "req_qty")
    @ApiModelProperty(value = "需求数量")
    private BigDecimal reqQty;
    /**
     * 领料数量
     */
    @Column(name = "issued_qty")
    @ApiModelProperty(value = "领料数量")
    private BigDecimal issuedQty;
    /**
     * 行状态
     */
    @Column(name = "line_status")
    @ApiModelProperty(value = "行状态")
    private String lineStatus;
    /**
     * 过账时间
     */
    @Column(name = "post_date")
    @ApiModelProperty(value = "过账时间")
    private Date postDate;
    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商")
    private String supplier;
    /**
     * 序列号
     */
    @Column(name = "serial_no")
    @ApiModelProperty(value = "序列号")
    private String serialNo;
    /**
     * 处理结果
     */
    @Column(name = "process_result")
    @ApiModelProperty(value = "处理结果")
    private String processResult;
    /**
     * 不良原因
     */
    @Column(name = "bad_mtr_reason")
    @ApiModelProperty(value = "不良原因")
    private String badMtrReason;
    /**
     * 不良描述
     */
    @Column(name = "bad_mtr_desc")
    @ApiModelProperty(value = "不良描述")
    private String badMtrDesc;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String remark;
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
