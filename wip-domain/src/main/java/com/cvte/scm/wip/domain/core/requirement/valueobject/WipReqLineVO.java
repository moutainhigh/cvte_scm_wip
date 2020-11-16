package com.cvte.scm.wip.domain.core.requirement.valueobject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com tingyx96@qq.com
 * @since : 2020/1/3 11:38
 */
@Data
@Accessors(chain = true)
public class WipReqLineVO {

    @ApiModelProperty(value = "行ID")
    private String lineId;
    /**
     * 投料单头ID，来源于wip_req_header表。
     */
    @ApiModelProperty(value = "投料单头ID，来源于wip_req_header表。")
    private String headerId;
    /**
     * 库存组织
     */
    @ApiModelProperty(value = "库存组织")
    private String organizationId;
    /**
     * 工序号
     */
    @ApiModelProperty(value = "工序号")
    private String wkpNo;
    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String lotNumber;
    /**
     * 位号
     */
    @ApiModelProperty(value = "位号")
    private String posNo;
    /**
     * 物料编码
     */
    @ApiModelProperty(value = "物料编码")
    private String itemNo;

    @ApiModelProperty(value = "物料描述")
    private String itemSpec;

    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 单位用量
     */
    @ApiModelProperty(value = "单位用量")
    private Double unitQty;
    /**
     * 需求数量
     */
    @ApiModelProperty(value = "需求数量")
    private Long reqQty;
    /**
     * 已发料数量
     */
    @ApiModelProperty(value = "已发料数量")
    private Long issuedQty;
    /**
     * 超发原因
     */
    @ApiModelProperty(value = "超发原因")
    private String exceedReason;
    /**
     * 原始行ID
     */
    @ApiModelProperty(value = "原始行ID")
    private String originalLineId;
    /**
     * 变更前行ID
     */
    @ApiModelProperty(value = "变更前行ID")
    private String beforeLineId;
    /**
     * 行状态
     */
    @ApiModelProperty(value = "行状态")
    private String lineStatus;
    /**
     * 变更原因
     */
    @ApiModelProperty(value = "变更原因")
    private String changeReason;
    @ApiModelProperty(value = "行版本，主要为了标记被取消数据，如果数据行被取消，则将行ID记录其中，否则为空")
    private String lineVersion;
    @ApiModelProperty(value = "来源编号")
    private String sourceCode = "SCM-WIP";
    @ApiModelProperty(value = "生效时间")
    private Date enableDate;
    @ApiModelProperty(value = "失效时间")
    private Date disableDate;
    private String rmk01;
    private String rmk02;
    private String rmk03;
    private String rmk04;
    private String rmk05;
    private String rmk06;
    private String rmk07;
    private String rmk08;
    private String rmk09;
    private String rmk10;
    private String rmk11;
    private String rmk12;
    private String rmk13;
    private String rmk14;
    private String rmk15;
    private String crtUser;
    private Date crtDate;
    private String updUser;
    private Date updDate;
    /**
     * 子节点
     */
    private List<WipReqLineVO> children;
    /**
     * 工厂ID
     */
    private String factoryId;
}
