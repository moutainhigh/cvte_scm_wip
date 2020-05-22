package com.cvte.scm.wip.domain.core.ckd.entity;


import com.cvte.csb.validator.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-05-20
 */
@Table(name = "wip_mc_req")
@ApiModel(description = "${table.comment}")
@Data
@EqualsAndHashCode
public class WipMcReqEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "mc_req_id")
    @ApiModelProperty(value = "主键")
    private String mcReqId;
    /**
     * 配料需求编号
     */
    @Column(name = "mc_req_no")
    @ApiModelProperty(value = "配料需求编号")
    private String mcReqNo;
    /**
     * 状态(110：正常，130：作废)
     */
    @ApiModelProperty(value = "状态(110：正常，130：作废)")
    private String status;
    /**
     * 库存组织
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "库存组织")
    private String organizationId;
    /**
     * 原始单号
     */
    @Column(name = "source_no")
    @ApiModelProperty(value = "原始单号")
    private String sourceNo;
    /**
     * 事业部
     */
    @Column(name = "bu_name")
    @ApiModelProperty(value = "事业部")
    private String buName;
    /**
     * 工厂id
     */
    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂id")
    private String factoryId;
    /**
     * 销售
     */
    @ApiModelProperty(value = "销售")
    private String sales;
    /**
     * 收货区域
     */
    @Column(name = "receive_region")
    @ApiModelProperty(value = "收货区域")
    private String receiveRegion;
    /**
     * 客户
     */
    @Column(name = "client_name")
    @ApiModelProperty(value = "客户")
    private String clientName;
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
    /**
     * 订单类型（订单所属战队）
     */
    @Column(name = "order_type")
    @ApiModelProperty(value = "订单类型（订单所属战队）")
    private String orderType;
    /**
     * 销管
     */
    @ApiModelProperty(value = "销管")
    private String assistant;
    /**
     * 原始单id
     */
    @Column(name = "source_id")
    @ApiModelProperty(value = "原始单id")
    private String sourceId;
    /**
     * 齐套日期
     */
    @Column(name = "mtr_ready_time")
    @ApiModelProperty(value = "齐套日期")
    private Date mtrReadyTime;
    /**
     * 计划发运日期
     */
    @Column(name = "plan_delivery_time")
    @ApiModelProperty(value = "计划发运日期")
    private Date planDeliveryTime;
    /**
     * 下采购日期
     */
    @Column(name = "to_pur_time")
    @ApiModelProperty(value = "下采购日期")
    private Date toPurTime;
    /**
     * 部门
     */
    @Column(name = "dept_name")
    @ApiModelProperty(value = "部门")
    private String deptName;
    /**
     * 下采购标识（1：已下，0：未下）
     */
    @ApiModelProperty(value = "下采购标识（1：已下，0：未下）")
    private String purchaseed;
    /**
     * 下生产标识（1：已下，0：未下）
     */
    @ApiModelProperty(value = "下生产标识（1：已下，0：未下）")
    private String produced;
    /**
     * 物料id
     */
    @Column(name = "item_id")
    @ApiModelProperty(value = "物料id")
    private String itemId;
    /**
     * 数量
     */
    @Column(name = "mc_qty")
    @ApiModelProperty(value = "数量")
    private Integer mcQty;
    /**
     * 原始行id
     */
    @Column(name = "source_line_id")
    @ApiModelProperty(value = "原始行id")
    private String sourceLineId;
    /**
     * 原始行编号
     */
    @Column(name = "source_line_no")
    @ApiModelProperty(value = "原始行编号")
    private String sourceLineNo;
    /**
     * 是否出库存（1：是，0：否）
     */
    @Column(name = "from_onhand")
    @ApiModelProperty(value = "是否出库存（1：是，0：否）")
    private String fromOnhand;

}
