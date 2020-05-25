package com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject;


import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleAdaptEntity;
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
@Table(name = "wip.wip_sub_rule_adapt")
@ApiModel(description = "临时代用规则适用表")
public class WipSubRuleAdaptDO {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "adapt_rule_id")
    @ApiModelProperty(value = "${field.comment}")
    private String adaptRuleId;
    /**
     * 规则ID
     */
    @Column(name = "rule_id")
    @ApiModelProperty(value = "规则ID")
    private String ruleId;
    /**
     * 范围类型：BOM（BOM）、研发型号（development_model）、生产批次（production_lot）和下单客户（order_customer）
     */
    @Column(name = "scope_type")
    @ApiModelProperty(value = "范围类型：BOM（BOM）、研发型号（development_model）、生产批次（production_lot）和下单客户（order_customer）")
    private String scopeType;
    /**
     * 适用类型：等于（equal） 、包含（include）、不包含（exclude）、取并集（union）
     */
    @Column(name = "adapt_type")
    @ApiModelProperty(value = "适用类型：等于（equal） 、包含（include）、不包含（exclude）、取并集（union）")
    private String adaptType;
    /**
     * 适用对象
     */
    @Column(name = "adapt_item")
    @ApiModelProperty(value = "适用对象")
    private String adaptItem;
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

    public static WipSubRuleAdaptEntity buildEntity(WipSubRuleAdaptDO ruleWfDO) {
        WipSubRuleAdaptEntity ruleWfEntity = new WipSubRuleAdaptEntity();
        BeanUtils.copyProperties(ruleWfDO, ruleWfEntity);
        return ruleWfEntity;
    }

    public static WipSubRuleAdaptDO buildDO(WipSubRuleAdaptEntity ruleWfEntity) {
        WipSubRuleAdaptDO ruleWfDO = new WipSubRuleAdaptDO();
        BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
        return ruleWfDO;
    }

    public static List<WipSubRuleAdaptEntity> batchBuildEntity(List<WipSubRuleAdaptDO> headerDOList) {
        List<WipSubRuleAdaptEntity> headerEntityList = new ArrayList<>();
        for (WipSubRuleAdaptDO ruleWfDO : headerDOList) {
            WipSubRuleAdaptEntity ruleWfEntity = buildEntity(ruleWfDO);
            headerEntityList.add(ruleWfEntity);
        }
        return headerEntityList;
    }

    public static List<WipSubRuleAdaptDO> batchBuildDO(List<WipSubRuleAdaptEntity> headerEntityList) {
        List<WipSubRuleAdaptDO> headerDOList = new ArrayList<>();
        for (WipSubRuleAdaptEntity ruleWfEntity : headerEntityList) {
            WipSubRuleAdaptDO ruleWfDO = new WipSubRuleAdaptDO();
            BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
            headerDOList.add(ruleWfDO);
        }
        return headerDOList;
    }
    
}