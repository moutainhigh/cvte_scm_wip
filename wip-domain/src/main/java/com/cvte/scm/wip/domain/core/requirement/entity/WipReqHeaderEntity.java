package com.cvte.scm.wip.domain.core.requirement.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Objects;

/**
 * @author jf
 * @since 2019-12-30
 */
@Data
@Accessors(chain = true)
public class WipReqHeaderEntity {

    private String headerId;

    @ApiModelProperty(value = "业务组织")
    private String organizationId;

    @ApiModelProperty(value = "单据号")
    private String billNo;

    @ApiModelProperty(value = "单据状态，10：草稿；20：已确认；30:已发料；40：已关闭；90：已取消。")
    private String billStatus;

    @ApiModelProperty(value = "单据类型，1：标准投料单；2：非标投料单。")
    private String billType;

    @ApiModelProperty(value = "数量")
    private Integer billQty;

    @ApiModelProperty(value = "来源类型，10：系统创建；20：手工创建；30：变更单拆单创建。")
    private String sourceType;

    @ApiModelProperty(value = "来源工单号")
    private String sourceNo;

    @ApiModelProperty(value = "${field.comment}")
    private String productId;

    @ApiModelProperty(value = "产品编号")
    private String productNo;

    @ApiModelProperty(value = "产品型号")
    private String productModel;

    @ApiModelProperty(value = "工厂")
    private String factoryId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "软件编号")
    private String softwareCode;

    @ApiModelProperty(value = "单据责任人")
    private String billPerson;

    @ApiModelProperty(value = "计划开工日期")
    private Date planStartDate;

    @ApiModelProperty(value = "物料齐套日期")
    private Date mtrReadyDate;

    @ApiModelProperty(value = "要求完工日期")
    private Date completionDate;

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

    @ApiModelProperty(value = "来源工单ID")
    private String sourceId;

    @ApiModelProperty(value = "BOM版本号")
    private String bomVersion;

    @ApiModelProperty(value = "EBS生产批次号")
    private String sourceLotNo;

    @ApiModelProperty(value = "客户ID")
    private String customerId;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof WipReqHeaderEntity) {
            WipReqHeaderEntity wipReqHeaderEntity = (WipReqHeaderEntity) obj;
            return Objects.equals(this.organizationId, wipReqHeaderEntity.organizationId) &&
                    Objects.equals(this.billNo, wipReqHeaderEntity.billNo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.organizationId);
        result = 31 * result + Objects.hashCode(this.billNo);
        return result;
    }
}
