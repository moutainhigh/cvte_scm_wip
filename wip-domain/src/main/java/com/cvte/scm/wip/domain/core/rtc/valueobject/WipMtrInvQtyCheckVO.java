package com.cvte.scm.wip.domain.core.rtc.valueobject;

import com.cvte.csb.toolkit.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

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
        StringBuilder errMsgBuilder = new StringBuilder();
        for (WipMtrInvQtyCheckVO invQtyCheckVO : invQtyCheckVOS) {
            errMsgBuilder.append("物料").append(invQtyCheckVO.getItemNo()).append("工序").append(invQtyCheckVO.getWkpNo()).append("子库").append(invQtyCheckVO.getInvpNo());
            if (StringUtils.isNotBlank(invQtyCheckVO.getLotNumber())) {
                errMsgBuilder.append("批次").append(invQtyCheckVO.getLotNumber());
            }
            errMsgBuilder.append(invQtyCheckVO.getErrMsg()).append(";");
        }

        return errMsgBuilder.toString();
    }

}
