package com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject;


import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleItemEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jf
 * @since 2020-02-17
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Table(name = "wip.wip_sub_rule_item")
@ApiModel(description = "临时代用规则物料表")
public class WipSubRuleItemDO {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "item_rule_id")
    @ApiModelProperty(value = "${field.comment}")
    private String itemRuleId;
    /**
     * 规则ID
     */
    @Column(name = "rule_id")
    @ApiModelProperty(value = "规则ID")
    private String ruleId;
    /**
     * 替换前物料ID
     */
    @Column(name = "before_item_id")
    @ApiModelProperty(value = "替换前物料ID")
    private String beforeItemId;
    /**
     * 替换后物料ID
     */
    @Column(name = "after_item_id")
    @ApiModelProperty(value = "替换后物料ID")
    private String afterItemId;
    /**
     * ${field.comment}
     */
    @Column(name = "rmk01")
    @ApiModelProperty(value = "临时作为替换前物料编码")
    private String rmk01;
    /**
     * ${field.comment}
     */
    @Column(name = "rmk02")
    @ApiModelProperty(value = "临时作为替换后物料编码")
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

    public static WipSubRuleItemEntity buildEntity(WipSubRuleItemDO ruleWfDO) {
        WipSubRuleItemEntity ruleWfEntity = new WipSubRuleItemEntity();
        BeanUtils.copyProperties(ruleWfDO, ruleWfEntity);
        return ruleWfEntity;
    }

    public static WipSubRuleItemDO buildDO(WipSubRuleItemEntity ruleWfEntity) {
        WipSubRuleItemDO ruleWfDO = new WipSubRuleItemDO();
        BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
        return ruleWfDO;
    }

    public static List<WipSubRuleItemEntity> batchBuildEntity(List<WipSubRuleItemDO> headerDOList) {
        List<WipSubRuleItemEntity> headerEntityList = new ArrayList<>();
        for (WipSubRuleItemDO ruleWfDO : headerDOList) {
            WipSubRuleItemEntity ruleWfEntity = buildEntity(ruleWfDO);
            headerEntityList.add(ruleWfEntity);
        }
        return headerEntityList;
    }

    public static List<WipSubRuleItemDO> batchBuildDO(List<WipSubRuleItemEntity> headerEntityList) {
        List<WipSubRuleItemDO> headerDOList = new ArrayList<>();
        for (WipSubRuleItemEntity ruleWfEntity : headerEntityList) {
            WipSubRuleItemDO ruleWfDO = new WipSubRuleItemDO();
            BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
            headerDOList.add(ruleWfDO);
        }
        return headerDOList;
    }

}