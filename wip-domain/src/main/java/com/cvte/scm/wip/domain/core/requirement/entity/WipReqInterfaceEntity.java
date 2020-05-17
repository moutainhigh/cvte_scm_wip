package com.cvte.scm.wip.domain.core.requirement.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jf
 * @since 2019-12-30
 */
@Data
@Accessors(chain = true)
public class WipReqInterfaceEntity {

    private String interfaceInId;

    private String groupId;

    private String organizationId;

    @ApiModelProperty(value = "投料单头ID，来源于wip_req_header表。")
    private String headerId;

    @ApiModelProperty(value = "投料单行ID，来源于wip_req_lines表。")
    private String lineId;

    @ApiModelProperty(value = "工单号")
    private String moNumber;

    @ApiModelProperty(value = "批次号")
    private String lotNumber;

    @ApiModelProperty(value = "位号")
    private String posNo;

    @ApiModelProperty(value = "物料编号")
    private String itemNo;

    @ApiModelProperty(value = "工序号")
    private String wkpNo;

    @ApiModelProperty(value = "新物料编号")
    private String itemNoNew;

    @ApiModelProperty(value = "需求数量")
    private Integer itemQty;

    @ApiModelProperty(value = "执行情况")
    private String proceed;

    @ApiModelProperty(value = "执行异常原因")
    private String exceptionReason;

    @ApiModelProperty(value = "变更类型，add、delete、update和replace")
    private String changeType;

    @ApiModelProperty(value = "来源单号")
    private String sourceBillNo;

    @ApiModelProperty(value = "变更来源")
    private String sourceCode;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @ApiModelProperty(value = "投料单行已备料，是否需要手工确认执行变更操作")
    private String needConfirm;

    @ApiModelProperty(value = "单位数量")
    private Double unitQty;

    @ApiModelProperty(value = "备料数量")
    private Integer issuedQty;

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

}
