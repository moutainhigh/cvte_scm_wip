package com.cvte.scm.wip.infrastructure.rework.mapper.dataobject;

import com.cvte.csb.validator.entity.BaseEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/16 16:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Table( name = "wip_rwk_mo_v")
@ApiModel(description = "WIP工单视图")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WipRwkMoDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "source_id")
    @ApiModelProperty(value = "工单ID")
    private Long sourceId;

    @Column(name = "organization_id")
    @ApiModelProperty(value = "库存组织ID")
    private Integer organizationId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_no")
    @ApiModelProperty(value = "产品编号")
    private String productNo;

    @Column(name = "product_model")
    @ApiModelProperty(value = "产品型号")
    private String productModel;

    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂ID")
    private Integer factoryId;

    @Column(name = "consumer_name")
    @ApiModelProperty(value = "客户简称")
    private String consumerName;

    @Column(name = "source_lot_no")
    @ApiModelProperty(value = "工单批次号")
    private String sourceLotNo;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "om_name")
    @ApiModelProperty(value = "内勤")
    private String omName;

    @Column(name = "product_name")
    @ApiModelProperty(value = "产品名称")
    private String productName;

    @Column(name = "cust_item_num")
    @ApiModelProperty(value = "客户产品编号")
    private String custItemNum;

    @Column(name = "cust_model")
    @ApiModelProperty(value = "客户产品型号")
    private String custModel;

    @Column(name = "cust_lot_number")
    @ApiModelProperty(value = "客户批次号")
    private String custLotNumber;

    @Column(name = "lot_status")
    @ApiModelProperty(value = "批次状态")
    private String lotStatus;

    @ApiModelProperty(value = "在库天数")
    private Long availableDays;

    @ApiModelProperty(value = "可用库存数量")
    private Long invStockQty;

    @ApiModelProperty(value = "即时库存")
    private Long onhandQty;

    @ApiModelProperty(value = "累计库存更改单未领料数量")
    private Long cnQtyRequired;

    @ApiModelProperty(value = "理论库存")
    private Long reStockQty;

    public static WipReworkMoEntity buildEntity(WipRwkMoDO billHDO) {
        WipReworkMoEntity billHeaderEntity = new WipReworkMoEntity();
        BeanUtils.copyProperties(billHDO, billHeaderEntity);
        return billHeaderEntity;
    }

    public static WipRwkMoDO buildDO(WipReworkMoEntity billHeaderEntity) {
        WipRwkMoDO billHDO = new WipRwkMoDO();
        BeanUtils.copyProperties(billHeaderEntity, billHDO);
        return billHDO;
    }

    public static List<WipReworkMoEntity> batchBuildEntity(List<WipRwkMoDO> billLDOList) {
        List<WipReworkMoEntity> billLineEntityList = new ArrayList<>();
        for (WipRwkMoDO billLDO : billLDOList) {
            WipReworkMoEntity billHeaderEntity = buildEntity(billLDO);
            billLineEntityList.add(billHeaderEntity);
        }
        return billLineEntityList;
    }

    public static List<WipRwkMoDO> batchBuildDO(List<WipReworkMoEntity> billLineEntityList) {
        List<WipRwkMoDO> billLDOList = new ArrayList<>();
        for (WipReworkMoEntity billLEntity : billLineEntityList) {
            WipRwkMoDO billHeaderEntity = buildDO(billLEntity);
            billLDOList.add(billHeaderEntity);
        }
        return billLDOList;
    }

}
