package com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import com.cvte.csb.validator.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * ${table.comment}
 *
 * @author author
 * @since 2020-06-11
 */
@Table(name = "wip_cn_bill_error_log")
@ApiModel(description = "更改单错误日志")
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WipCnBillErrorLogDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "log_id")
    @ApiModelProperty(value = "${field.comment}")
    private String logId;
    /**
     * 更改单ID
     */
    @Column(name = "bill_id")
    @ApiModelProperty(value = "更改单ID")
    private String billId;
    /**
     * 更改单号
     */
    @Column(name = "bill_no")
    @ApiModelProperty(value = "更改单号")
    private String billNo;
    /**
     * 操作类型
     */
    @Column(name = "opt_type")
    @ApiModelProperty(value = "操作类型")
    private String optType;
    /**
     * 工单ID
     */
    @Column(name = "mo_id")
    @ApiModelProperty(value = "工单ID")
    private String moId;
    /**
     * 批次
     */
    @Column(name = "mo_lot_no")
    @ApiModelProperty(value = "批次")
    private String moLotNo;
    /**
     * 错误信息
     */
    @Column(name = "error_message")
    @ApiModelProperty(value = "错误信息")
    private String errorMessage;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String status;
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

}
