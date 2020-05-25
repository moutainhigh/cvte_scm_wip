package com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject;


import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jf
 * @since 2020-02-13
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Table(name = "wip.wip_sub_rule")
@ApiModel(description = "临时代用单规则表")
public class WipSubRuleDO {

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "rule_id")
    @ApiModelProperty(value = "规则ID")
    private String ruleId;

    @Column(name = "rule_no")
    @ApiModelProperty(value = "规则编号")
    private String ruleNo;

    @Column(name = "rule_status")
    @ApiModelProperty(value = "规则状态")
    private String ruleStatus;

    @Column(name = "rule_reason_type")
    @ApiModelProperty(value = "替代原因：消耗呆料/物料备份/品质问题/研发变更/商务需求")
    private String ruleReasonType;

    @Column(name = "rule_reason_remark")
    @ApiModelProperty(value = "替代原因详情")
    private String ruleReasonRemark;

    @Column(name = "if_pr")
    @ApiModelProperty(value = "是否产生PR Y/N")
    private String ifPr;

    @Column(name = "if_mix")
    @ApiModelProperty(value = "是否混料  Y/N")
    private String ifMix;

    @Column(name = "if_couple")
    @ApiModelProperty(value = "是否成套替代  Y/N")
    private String ifCouple;

    @Column(name = "enable_time")
    @ApiModelProperty(value = "生效时间")
    private Date enableTime;

    @Column(name = "disable_time")
    @ApiModelProperty(value = "失效时间")
    private Date disableTime;

    @Column(name = "confirm_time")
    @ApiModelProperty(value = "审核时间")
    private Date confirmTime;

    @Column(name = "process_id")
    @ApiModelProperty(value = "审核流ID")
    private String processId;

    @Column(name = "rmk01")
    @ApiModelProperty(value = "${field.comment}")
    private String rmk01;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk02;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk03;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk04;

    @ApiModelProperty(value = "${field.comment}")
    private String rmk05;

    @Column(name = "crt_user")
    @ApiModelProperty(value = "创建人/申请人")
    private String crtUser;

    @Column(name = "crt_time")
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    @Column(name = "upd_user")
    @ApiModelProperty(value = "更新人")
    private String updUser;

    @Column(name = "upd_time")
    @ApiModelProperty(value = "更新时间")
    private Date updTime;

    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织ID")
    private String organizationId;

    public static WipSubRuleEntity buildEntity(WipSubRuleDO ruleWfDO) {
        WipSubRuleEntity ruleWfEntity = new WipSubRuleEntity();
        BeanUtils.copyProperties(ruleWfDO, ruleWfEntity);
        return ruleWfEntity;
    }

    public static WipSubRuleDO buildDO(WipSubRuleEntity ruleWfEntity) {
        WipSubRuleDO ruleWfDO = new WipSubRuleDO();
        BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
        return ruleWfDO;
    }

    public static List<WipSubRuleEntity> batchBuildEntity(List<WipSubRuleDO> headerDOList) {
        List<WipSubRuleEntity> headerEntityList = new ArrayList<>();
        for (WipSubRuleDO ruleWfDO : headerDOList) {
            WipSubRuleEntity ruleWfEntity = buildEntity(ruleWfDO);
            headerEntityList.add(ruleWfEntity);
        }
        return headerEntityList;
    }

    public static List<WipSubRuleDO> batchBuildDO(List<WipSubRuleEntity> headerEntityList) {
        List<WipSubRuleDO> headerDOList = new ArrayList<>();
        for (WipSubRuleEntity ruleWfEntity : headerEntityList) {
            WipSubRuleDO ruleWfDO = new WipSubRuleDO();
            BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
            headerDOList.add(ruleWfDO);
        }
        return headerDOList;
    }

}