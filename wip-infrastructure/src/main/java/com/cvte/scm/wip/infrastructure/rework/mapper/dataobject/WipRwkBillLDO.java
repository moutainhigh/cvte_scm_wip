package com.cvte.scm.wip.infrastructure.rework.mapper.dataobject;

import com.cvte.csb.sys.base.utils.BeanUtils;
import com.cvte.csb.validator.entity.BaseEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
  * @since    : 2020/5/16 17:02
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Table(name = "wip_rwk_bill_l")
@ApiModel(description = "返工单行")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WipRwkBillLDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 单据行ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "bill_line_id")
    @ApiModelProperty(value = "单据行ID")
    private String billLineId;
    /**
     * 返工单ID
     */
    @Column(name = "bill_id")
    @ApiModelProperty(value = "返工单ID")
    private String billId;
    /**
     * 工单ID
     */
    @Column(name = "mo_id")
    @ApiModelProperty(value = "工单ID")
    private String moId;
    /**
     * 生产批次
     */
    @Column(name = "mo_lot_no")
    @ApiModelProperty(value = "生产批次")
    private String moLotNo;
    /**
     * 批次状态
     */
    @Column(name = "mo_lot_status")
    @ApiModelProperty(value = "批次状态")
    private String moLotStatus;
    /**
     * 产品型号
     */
    @Column(name = "product_model")
    @ApiModelProperty(value = "产品型号")
    private String productModel;
    /**
     * 返工数量
     */
    @Column(name = "mo_rework_qty")
    @ApiModelProperty(value = "返工数量")
    private Long moReworkQty;
    /**
     * 改前物料
     */
    @Column(name = "pri_product_no")
    @ApiModelProperty(value = "改前成品编码")
    private String priProductNo;
    /**
     * 改后物料
     */
    @Column(name = "sub_product_no")
    @ApiModelProperty(value = "改后成品编码")
    private String subProductNo;
    /**
     * 数据状态
     */
    @Column(name = "status")
    @ApiModelProperty(value = "数据状态")
    private String status;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
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

    public static WipReworkBillLineEntity buildEntity(WipRwkBillLDO billHDO) {
        WipReworkBillLineEntity billHeaderEntity = new WipReworkBillLineEntity();
        BeanUtils.copyProperties(billHDO, billHeaderEntity);
        return billHeaderEntity;
    }

    public static WipRwkBillLDO buildDO(WipReworkBillLineEntity billHeaderEntity) {
        WipRwkBillLDO billHDO = new WipRwkBillLDO();
        BeanUtils.copyProperties(billHeaderEntity, billHDO);
        return billHDO;
    }

    public static List<WipReworkBillLineEntity> batchBuildEntity(List<WipRwkBillLDO> billLDOList) {
        List<WipReworkBillLineEntity> billLineEntityList = new ArrayList<>();
        for (WipRwkBillLDO billLDO : billLDOList) {
            WipReworkBillLineEntity billHeaderEntity = buildEntity(billLDO);
            billLineEntityList.add(billHeaderEntity);
        }
        return billLineEntityList;
    }

    public static List<WipRwkBillLDO> batchBuildDO(List<WipReworkBillLineEntity> billLineEntityList) {
        List<WipRwkBillLDO> billLDOList = new ArrayList<>();
        for (WipReworkBillLineEntity billLEntity : billLineEntityList) {
            WipRwkBillLDO billHeaderEntity = buildDO(billLEntity);
            billLDOList.add(billHeaderEntity);
        }
        return billLDOList;
    }

}
