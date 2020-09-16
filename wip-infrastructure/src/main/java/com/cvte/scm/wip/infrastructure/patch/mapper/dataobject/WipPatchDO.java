package com.cvte.scm.wip.infrastructure.patch.mapper.dataobject;


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
 * ${table.comment}
 *
 * @author author
 * @since 2020-07-25
 */
@Table(name = "wip_patch")
@ApiModel(description = "${table.comment}")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WipPatchDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 工厂编号+年（4位）+月（2位）+日（2位）+流水号（4位），例如CYSR202007150001
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "bill_id")
    @ApiModelProperty(value = "工厂编号+年（4位）+月（2位）+日（2位）+流水号（4位），例如CYSR202007150001")
    private String billId;
    /**
     * 组织id
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织id")
    private String organizationId;
    /**
     * 工厂id
     */
    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂id")
    private String factoryId;
    /**
     * 工单ID
     */
    @Column(name = "mo_id")
    @ApiModelProperty(value = "工单ID")
    private String moId;
    /**
     * 工单批次号
     */
    @Column(name = "mo_lot_no")
    @ApiModelProperty(value = "工单批次号")
    private String moLotNo;
    /**
     * 物料ID
     */
    @Column(name = "item_id")
    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 单据类型
     */
    @Column(name = "bill_type")
    @ApiModelProperty(value = "单据类型")
    private String billType;
    /**
     * 状态行ID
     */
    @Column(name = "wf_id")
    @ApiModelProperty(value = "状态行ID")
    private Integer wfId;
    /**
     * 补料原因，字典：未配
     */
    @Column(name = "pur_explain")
    @ApiModelProperty(value = "补料原因，字典：未配")
    private String purExplain;
    /**
     * 投料单头ID
     */
    @Column(name = "header_id")
    @ApiModelProperty("投料单头ID")
    private String headerId;
    /**
     * 齐套日期
     */
    @Column(name = "mtr_ready_date")
    @ApiModelProperty(value = "齐套日期")
    private Date mtrReadyDate;
    /**
     * ${field.comment}
     */
    @Column(name = "rmk")
    @ApiModelProperty(value = "备注")
    private String rmk;

}
