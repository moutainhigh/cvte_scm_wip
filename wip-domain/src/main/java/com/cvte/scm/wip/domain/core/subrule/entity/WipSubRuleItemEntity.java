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
public class WipSubRuleItemEntity {

    private String itemRuleId;

    @ApiModelProperty(value = "规则ID")
    private String ruleId;

    @ApiModelProperty(value = "替换前物料ID")
    private String beforeItemId;

    @ApiModelProperty(value = "替换后物料ID")
    private String afterItemId;

    @ApiModelProperty(value = "临时作为替换前物料编码")
    private String rmk01;

    @ApiModelProperty(value = "临时作为替换后物料编码")
    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

}