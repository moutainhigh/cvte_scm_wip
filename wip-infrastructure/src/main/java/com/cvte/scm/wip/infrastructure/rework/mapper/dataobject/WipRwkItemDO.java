package com.cvte.scm.wip.infrastructure.rework.mapper.dataobject;

import com.cvte.csb.validator.entity.BaseEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import io.swagger.annotations.ApiModel;
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
  * @since    : 2020/8/10 10:12
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Table( name = "wip_rwk_item_v")
@ApiModel(description = "WIP_01仓视图")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WipRwkItemDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "factory_number")
    private String factoryNumber;

    @Column(name = "onhand_qty")
    private Long onhandQty;

    @Column(name = "re_stock_qty")
    private Long reStockQty;

    @Column(name = "inv_stock_qty")
    private Long invStockQty;

    @Column(name = "available_days")
    private Long availableDays;

    @Column(name = "factory_id")
    private Integer factoryId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_no")
    private String itemNo;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "product_model")
    private String productModel;

    public static WipReworkMoEntity buildEntity(WipRwkItemDO itemDO) {
        WipReworkMoEntity billHeaderEntity = new WipReworkMoEntity();
        BeanUtils.copyProperties(itemDO, billHeaderEntity);
        billHeaderEntity.setOrganizationId(itemDO.getOrganizationId())
                .setOnhandQty(itemDO.getOnhandQty())
                .setReStockQty(itemDO.getReStockQty())
                .setInvStockQty(itemDO.getInvStockQty())
                .setAvailableDays(itemDO.getAvailableDays())
                .setFactoryId(itemDO.getFactoryId())
                .setProductId(itemDO.getItemId())
                .setProductNo(itemDO.getItemNo())
                .setProductName(itemDO.getItemName())
                .setProductModel(itemDO.getProductModel());
        return billHeaderEntity;
    }

    public static List<WipReworkMoEntity> batchBuildEntity(List<WipRwkItemDO> itemList) {
        List<WipReworkMoEntity> billLineEntityList = new ArrayList<>();
        for (WipRwkItemDO itemDO : itemList) {
            WipReworkMoEntity billHeaderEntity = buildEntity(itemDO);
            billLineEntityList.add(billHeaderEntity);
        }
        return billLineEntityList;
    }

}
