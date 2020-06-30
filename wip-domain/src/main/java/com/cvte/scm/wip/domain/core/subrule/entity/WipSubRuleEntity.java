package com.cvte.scm.wip.domain.core.subrule.entity;


import com.cvte.scm.wip.domain.core.subrule.valueobject.ReviewerVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jf
 * @since 2020-02-13
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipSubRuleEntity {

    private String ruleId;

    @ApiModelProperty(value = "规则编号")
    private String ruleNo;

    @ApiModelProperty(value = "规则状态")
    private String ruleStatus;

    @ApiModelProperty(value = "替代原因：消耗呆料/物料备份/品质问题/研发变更/商务需求")
    private String ruleReasonType;

    @ApiModelProperty(value = "替代原因详情")
    private String ruleReasonRemark;

    @ApiModelProperty(value = "是否产生PR Y/N")
    private String ifPr;

    @ApiModelProperty(value = "是否混料  Y/N")
    private String ifMix;

    @ApiModelProperty(value = "是否成套替代  Y/N")
    private String ifCouple;

    @ApiModelProperty(value = "生效时间")
    private Date enableTime;

    @ApiModelProperty(value = "失效时间")
    private Date disableTime;

    @ApiModelProperty(value = "审核时间")
    private Date confirmTime;

    @ApiModelProperty(value = "审核流ID")
    private String processId;

    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    @ApiModelProperty(value = "创建人/申请人")
    private String crtUser;

    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    @ApiModelProperty(value = "更新人")
    private String updUser;

    @ApiModelProperty(value = "更新时间")
    private Date updTime;

    @ApiModelProperty(value = "组织ID")
    private String organizationId;

    @Transient
    private List<String[]> subItemNoList; // 数组长度为2，索引0为替换前物料编号，索引为1为替换后物料编号

    @Transient
    private Map<String, Map<String, String>> scopeMap; // key为适用范围，value的key为匹配属性，value为匹配值，通过分号分隔

    @Transient
    private Map<String, List<ReviewerVO>> reviewerMap; // key为审核人职位，value为多个审核人对象，字段有审核人ID和审核人名称

}