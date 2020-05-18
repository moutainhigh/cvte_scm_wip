package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author jf
 * @since 2019-12-30
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "投料单头表")
@Table(name = "wip.wip_req_header")
public class WipReqHeaderDO {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "header_id")
    @ApiModelProperty(value = "${field.comment}")
    private String headerId;
    /**
     * 业务组织
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "业务组织")
    private String organizationId;
    /**
     * 单据号
     */
    @Column(name = "bill_no")
    @ApiModelProperty(value = "单据号")
    private String billNo;
    /**
     * 单据状态，10：草稿；20：已确认；30:已发料；40：已关闭；90：已取消。
     */
    @Column(name = "bill_status")
    @ApiModelProperty(value = "单据状态，10：草稿；20：已确认；30:已发料；40：已关闭；90：已取消。")
    private String billStatus;
    /**
     * 单据类型，1：标准投料单；2：非标投料单。
     */
    @Column(name = "bill_type")
    @ApiModelProperty(value = "单据类型，1：标准投料单；2：非标投料单。")
    private String billType;
    /**
     * 数量
     */
    @Column(name = "bill_qty")
    @ApiModelProperty(value = "数量")
    private Integer billQty;
    /**
     * 来源类型，10：系统创建；20：手工创建；30：变更单拆单创建。
     */
    @Column(name = "source_type")
    @ApiModelProperty(value = "来源类型，10：系统创建；20：手工创建；30：变更单拆单创建。")
    private String sourceType;
    /**
     * 来源工单号
     */
    @Column(name = "source_no")
    @ApiModelProperty(value = "来源工单号")
    private String sourceNo;
    /**
     * ${field.comment}
     */
    @Column(name = "product_id")
    @ApiModelProperty(value = "${field.comment}")
    private String productId;
    /**
     * 产品编号
     */
    @Column(name = "product_no")
    @ApiModelProperty(value = "产品编号")
    private String productNo;
    /**
     * 产品型号
     */
    @Column(name = "product_model")
    @ApiModelProperty(value = "产品型号")
    private String productModel;
    /**
     * 工厂
     */
    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂")
    private String factoryId;
    /**
     * 客户名称
     */
    @Column(name = "client_name")
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    /**
     * 软件编号
     */
    @Column(name = "software_code")
    @ApiModelProperty(value = "软件编号")
    private String softwareCode;
    /**
     * 单据责任人
     */
    @Column(name = "bill_person")
    @ApiModelProperty(value = "单据责任人")
    private String billPerson;
    /**
     * 计划开工日期
     */
    @Column(name = "plan_start_date")
    @ApiModelProperty(value = "计划开工日期")
    private Date planStartDate;
    /**
     * 物料齐套日期
     */
    @Column(name = "mtr_ready_date")
    @ApiModelProperty(value = "物料齐套日期")
    private Date mtrReadyDate;
    /**
     * 要求完工日期
     */
    @Column(name = "completion_date")
    @ApiModelProperty(value = "要求完工日期")
    private Date completionDate;
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
    @ApiModelProperty(value = "${field.comment}")
    private String rmk06;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk07;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk08;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk09;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk10;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk11;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk12;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk13;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk14;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk15;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_date")
    @ApiModelProperty(value = "${field.comment}")
    private Date crtDate;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_user")
    @ApiModelProperty(value = "${field.comment}")
    private String updUser;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_date")
    @ApiModelProperty(value = "${field.comment}")
    private Date updDate;
    /**
     * 来源工单ID
     */
    @Column(name = "source_id")
    @ApiModelProperty(value = "来源工单ID")
    private String sourceId;
    /**
     * BOM版本号
     */
    @Column(name = "bom_version")
    @ApiModelProperty(value = "BOM版本号")
    private String bomVersion;
    /**
     * EBS生产批次号
     */
    @Column(name = "source_lot_no")
    @ApiModelProperty(value = "EBS生产批次号")
    private String sourceLotNo;
    /**
     * 客户ID
     */
    @Column(name = "customer_id")
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof WipReqHeaderDO) {
            WipReqHeaderDO wipReqHeaderDO = (WipReqHeaderDO) obj;
            return Objects.equals(this.organizationId, wipReqHeaderDO.organizationId) &&
                    Objects.equals(this.billNo, wipReqHeaderDO.billNo);
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

    public static WipReqHeaderEntity buildEntity(WipReqHeaderDO headerDO) {
        WipReqHeaderEntity headerEntity = new WipReqHeaderEntity();
        BeanUtils.copyProperties(headerDO, headerEntity);
        return headerEntity;
    }

    public static WipReqHeaderDO buildDO(WipReqHeaderEntity headerEntity) {
        WipReqHeaderDO headerDO = new WipReqHeaderDO();
        BeanUtils.copyProperties(headerEntity, headerDO);
        return headerDO;
    }

    public static List<WipReqHeaderEntity> batchBuildEntity(List<WipReqHeaderDO> headerDOList) {
        List<WipReqHeaderEntity> headerEntityList = new ArrayList<>();
        for (WipReqHeaderDO headerDO : headerDOList) {
            WipReqHeaderEntity headerEntity = buildEntity(headerDO);
            headerEntityList.add(headerEntity);
        }
        return headerEntityList;
    }

    public static List<WipReqHeaderDO> batchBuildDO(List<WipReqHeaderEntity> headerEntityList) {
        List<WipReqHeaderDO> headerDOList = new ArrayList<>();
        for (WipReqHeaderEntity headerEntity : headerEntityList) {
            WipReqHeaderDO headerDO = new WipReqHeaderDO();
            BeanUtils.copyProperties(headerEntity, headerDO);
            headerDOList.add(headerDO);
        }
        return headerDOList;
    }

}
