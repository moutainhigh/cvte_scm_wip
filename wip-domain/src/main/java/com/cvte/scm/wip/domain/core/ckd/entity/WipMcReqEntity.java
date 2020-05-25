package com.cvte.scm.wip.domain.core.ckd.entity;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import java.util.Date;

/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-05-20
 */
@Data
@EqualsAndHashCode
public class WipMcReqEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String mcReqId;
    /**
     * 配料需求编号
     */
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
    @ApiModelProperty(value = "库存组织")
    private String organizationId;
    /**
     * 原始单号
     */
    @ApiModelProperty(value = "原始单号")
    private String sourceNo;
    /**
     * 事业部
     */
    @ApiModelProperty(value = "事业部")
    private String buName;
    /**
     * 工厂id
     */
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
    @ApiModelProperty(value = "收货区域")
    private String receiveRegion;
    /**
     * 客户
     */
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
    private Date updTime;
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
     * 订单类型（订单所属战队）
     */
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
    @ApiModelProperty(value = "原始单id")
    private String sourceId;
    /**
     * 齐套日期
     */
    @ApiModelProperty(value = "齐套日期")
    private Date mtrReadyTime;
    /**
     * 计划发运日期
     */
    @ApiModelProperty(value = "计划发运日期")
    private Date planDeliveryTime;
    /**
     * 下采购日期
     */
    @ApiModelProperty(value = "下采购日期")
    private Date toPurTime;
    /**
     * 部门
     */
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
    @ApiModelProperty(value = "物料id")
    private String itemId;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer mcQty;
    /**
     * 原始行id
     */
    @ApiModelProperty(value = "原始行id")
    private String sourceLineId;
    /**
     * 原始行编号
     */
    @ApiModelProperty(value = "原始行编号")
    private String sourceLineNo;
    /**
     * 是否出库存（1：是，0：否）
     */
    @ApiModelProperty(value = "是否出库存（1：是，0：否）")
    private String fromOnhand;

}
