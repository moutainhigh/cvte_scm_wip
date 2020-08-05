package com.cvte.scm.wip.infrastructure.rework.mapper.dataobject;

import com.cvte.csb.validator.entity.BaseEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillHeaderEntity;
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
  * 
  * @author  : xueyuting
  * @since    : 2020/5/16 16:50
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Table(name = "wip_rwk_bill_h")
@ApiModel(description = "返工单")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WipRwkBillHDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 单据ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "bill_id")
    @ApiModelProperty(value = "单据ID")
    private String billId;
    /**
     * 单据号
     */
    @Column(name = "bill_no")
    @ApiModelProperty(value = "单据号")
    private String billNo;
    /**
     * 单据状态
     */
    @Column(name = "bill_status")
    @ApiModelProperty(value = "单据状态")
    private String billStatus;
    /**
     * 库存组织ID
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "库存组织ID")
    private String organizationId;
    /**
     * 工厂ID
     */
    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂ID")
    private String factoryId;
    /**
     * 返工类型
     */
    @Column(name = "rework_type")
    @ApiModelProperty(value = "返工类型")
    private String reworkType;
    /**
     * 更改原因
     */
    @Column(name = "rework_reason_type")
    @ApiModelProperty(value = "更改原因")
    private String reworkReasonType;
    /**
     * 更改原因详述
     */
    @Column(name = "rework_reason")
    @ApiModelProperty(value = "更改原因详述")
    private String reworkReason;
    /**
     * 更改要求
     */
    @Column(name = "rework_desc")
    @ApiModelProperty(value = "更改要求")
    private String reworkDesc;
    /**
     * 责任事业部
     */
    @Column(name = "resp_bu")
    @ApiModelProperty(value = "责任事业部")
    private String respBu;
    /**
     * 责任部门
     */
    @Column(name = "resp_dept")
    @ApiModelProperty(value = "责任部门")
    private String respDept;
    /**
     * 期望完成时间
     */
    @Column(name = "expect_finish_date")
    @ApiModelProperty(value = "期望完成时间")
    private Date expectFinishDate;
    /**
     * 预计出货时间
     */
    @Column(name = "expect_delivery_date")
    @ApiModelProperty(value = "预计出货时间")
    private Date expectDeliveryDate;
    /**
     * 销售订单号
     */
    @Column(name = "source_so_no")
    @ApiModelProperty(value = "销售订单号")
    private String sourceSoNo;
    /**
     * 客户
     */
    @Column(name = "consumer_name")
    @ApiModelProperty(value = "客户")
    private String consumerName;
    /**
     * 不良品处理方式
     */
    @Column(name = "reject_deal_type")
    @ApiModelProperty(value = "不良品处理方式")
    private String rejectDealType;
    /**
     * 良品处理方式
     */
    @Column(name = "good_deal_type")
    @ApiModelProperty(value = "良品处理方式")
    private String goodDealType;
    /**
     * 不良品物料处理方式
     */
    @Column(name = "reject_mtr_deal_type")
    @ApiModelProperty(value = "不良品物料处理方式")
    private String rejectMtrDealType;
    /**
     * 良品物料处理方式
     */
    @Column(name = "good_mtr_deal_type")
    @ApiModelProperty(value = "良品物料处理方式")
    private String goodMtrDealType;
    /**
     * 其他处理方式
     */
    @Column(name = "reject_deal_other_type")
    @ApiModelProperty(value = "其他处理方式")
    private String rejectDealOtherType;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String remark;
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
     * 创建人
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "创建人")
    private String crtUser;
    /**
     * 创建时间
     */
    @Column(name = "crt_date")
    @ApiModelProperty(value = "创建时间")
    private Date crtDate;
    /**
     * 更新人
     */
    @Column(name = "upd_user")
    @ApiModelProperty(value = "更新人")
    private String updUser;
    /**
     * 更新时间
     */
    @Column(name = "upd_date")
    @ApiModelProperty(value = "更新时间")
    private Date updDate;
    /**
     * 来源订单ID
     */
    @Column(name = "source_order_id")
    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;
    /**
     * 来源系统
     */
    @Column(name = "source_code")
    @ApiModelProperty(value = "来源系统")
    private String sourceCode;

    @Column(name = "error_caused_by")
    @ApiModelProperty(value = "失误造成者")
    private String errorCausedBy;

    @Column(name = "first_discoverer")
    @ApiModelProperty(value = "第一发现人")
    private String firstDiscoverer;

    @Column(name = "top_management")
    @ApiModelProperty(value = "参与该失误处理的最高管理层")
    private String topManagement;

    public static WipReworkBillHeaderEntity buildEntity(WipRwkBillHDO billHDO) {
        WipReworkBillHeaderEntity billHeaderEntity = new WipReworkBillHeaderEntity();
        BeanUtils.copyProperties(billHDO, billHeaderEntity);
        return billHeaderEntity;
    }

    public static WipRwkBillHDO buildDO(WipReworkBillHeaderEntity billHeaderEntity) {
        WipRwkBillHDO billHDO = new WipRwkBillHDO();
        BeanUtils.copyProperties(billHeaderEntity, billHDO);
        return billHDO;
    }

    public static List<WipReworkBillHeaderEntity> batchBuildEntity(List<WipRwkBillHDO> billLDOList) {
        List<WipReworkBillHeaderEntity> billLineEntityList = new ArrayList<>();
        for (WipRwkBillHDO billLDO : billLDOList) {
            WipReworkBillHeaderEntity billHeaderEntity = buildEntity(billLDO);
            billLineEntityList.add(billHeaderEntity);
        }
        return billLineEntityList;
    }

    public static List<WipRwkBillHDO> batchBuildDO(List<WipReworkBillHeaderEntity> billLineEntityList) {
        List<WipRwkBillHDO> billLDOList = new ArrayList<>();
        for (WipReworkBillHeaderEntity billLEntity : billLineEntityList) {
            WipRwkBillHDO billHeaderEntity = buildDO(billLEntity);
            billLDOList.add(billHeaderEntity);
        }
        return billLDOList;
    }

}
