package com.cvte.scm.wip.domain.common.user.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 适配五层架构,没有做领域设计改造, comment by xueyuting
 * @Author: wufeng
 * @Date: 2019/10/21 11:53
 */
@Data
public class OrgExtEntity {
    @ApiModelProperty(value = "拓展属性id", hidden = true)
    private String id;

    @ApiModelProperty(value = "是否虚拟组织", allowableValues = "0,1", example = "0")
    private String isVirtualOrg;

    @ApiModelProperty(value = "是否销售组织", allowableValues = "0,1", example = "0")
    private String isSalesOrg;

    @ApiModelProperty(value = "映射EBS编码", example = "ebs_code")
    private String ebsCode;

    @ApiModelProperty(value = "映射HCM编码", example = "hr_code")
    private String hrCode;

    @ApiModelProperty(value = "映射OA编码", example = "oa_code")
    private String oaCode;

    @ApiModelProperty(value = "映射4A编码", example = "4a_code")
    private String faCode;

    @ApiModelProperty(value = "映射其他编码", example = "other_code")
    private String otherCode;

    private String remark;

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private String crtUser;

    private String crtName;

    private String crtHost;

    private Date crtTime;

    private String updUser;

    private String updName;

    private String updHost;

    private Date updTime;

    private Integer version;

    private String receiveGl;

    private String incomeGl;

    private String corpsCode;

    private String ebsCorpCode;

    private BigDecimal eamCorpId;

    private BigDecimal eamOuId;
}
