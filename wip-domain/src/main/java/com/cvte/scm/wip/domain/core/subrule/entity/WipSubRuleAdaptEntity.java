package com.cvte.scm.wip.domain.core.subrule.entity;


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
 * @author jf
 * @since 2020-02-17
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipSubRuleAdaptEntity {

    private String adaptRuleId;

    private String ruleId;

    @ApiModelProperty(value = "范围类型：BOM（BOM）、研发型号（development_model）、生产批次（production_lot）和下单客户（order_customer）")
    private String scopeType;

    @ApiModelProperty(value = "适用类型：等于（equal） 、包含（include）、不包含（exclude）、取并集（union）")
    private String adaptType;

    @ApiModelProperty(value = "适用对象")
    private String adaptItem;

    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;
}