package com.cvte.scm.wip.domain.core.rtc.valueobject;

import com.cvte.csb.toolkit.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/18 15:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrInvQtyCheckVO {

    private String itemId;

    private String itemNo;

    private String wkpNo;

    private String invpNo;

    private String lotNumber;

    // 现有量, 为空时不会回写到界面
    private BigDecimal invQty;

    private BigDecimal availQty;

    private String errMsg;

    public static WipMtrInvQtyCheckVO buildItemSub(String itemId, String itemNo, String wkpNo, String invpNo, BigDecimal invQty, BigDecimal availQty, String errMsg) {
        return WipMtrInvQtyCheckVO.buildItemSubLot(itemId, itemNo, wkpNo, invpNo, null, invQty, availQty, errMsg);
    }

    public static WipMtrInvQtyCheckVO buildItemSubLot(String itemId, String itemNo, String wkpNo, String invpNo, String lotNumber, BigDecimal invQty, BigDecimal availQty, String errMsg) {
        WipMtrInvQtyCheckVO invQtyCheckVO = new WipMtrInvQtyCheckVO();
        invQtyCheckVO.setItemId(itemId)
                .setItemNo(itemNo)
                .setWkpNo(wkpNo)
                .setInvpNo(invpNo)
                .setLotNumber(lotNumber)
                .setInvQty(invQty)
                .setAvailQty(availQty)
                .setErrMsg(errMsg);
        return invQtyCheckVO;
    }

    public static String buildMsg(List<WipMtrInvQtyCheckVO> invQtyCheckVOS) {
        List<String> itemNoList = invQtyCheckVOS.stream().map(WipMtrInvQtyCheckVO::getItemNo).collect(Collectors.toList());
        return "物料" + String.join(",", itemNoList) + "现有量不足";
    }

}
