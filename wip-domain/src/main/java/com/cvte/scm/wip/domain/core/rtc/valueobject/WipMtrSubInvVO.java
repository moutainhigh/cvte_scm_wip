package com.cvte.scm.wip.domain.core.rtc.valueobject;

import com.cvte.scm.wip.common.utils.BatchProcessUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 12:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrSubInvVO {

    private String organizationId;

    private String factoryId;

    private String factoryNo;

    private String inventoryItemId;

    private String lotNumber;

    private Date firstStockinDate;

    private String subinventoryCode;

    private BigDecimal supplyQty;

    public static Map<String, BigDecimal> groupQtyByItem(List<WipMtrSubInvVO> subInvVOList) {
        return subInvVOList.stream().collect(Collectors.toMap(WipMtrSubInvVO::getInventoryItemId, WipMtrSubInvVO::getSupplyQty, BigDecimal::add));
    }

    public static Map<String, BigDecimal> groupQtyByItemSub(List<WipMtrSubInvVO> subInvVOList) {
        return subInvVOList.stream().collect(Collectors.toMap(item -> BatchProcessUtils.getKey(item.getInventoryItemId(), item.getSubinventoryCode()), WipMtrSubInvVO::getSupplyQty, BigDecimal::add));
    }

    public static Map<String, BigDecimal> groupQtyByItemSubLot(List<WipMtrSubInvVO> subInvVOList) {
        return subInvVOList.stream().collect(Collectors.toMap(item -> BatchProcessUtils.getKey(item.getInventoryItemId(), item.getSubinventoryCode(), item.getLotNumber()), WipMtrSubInvVO::getSupplyQty, BigDecimal::add));
    }

}
