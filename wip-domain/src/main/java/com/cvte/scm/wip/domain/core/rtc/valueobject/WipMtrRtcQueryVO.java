package com.cvte.scm.wip.domain.core.rtc.valueobject;

import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/8 16:16
 */
@Data
@Accessors(chain = true)
public class WipMtrRtcQueryVO {

    private String lineId;

    private String moId;

    private String headerId;

    private String organizationId;

    private String factoryId;

    private String itemId;

    private String itemNo;

    private String moLotNo;

    private String wkpNo;

    private String invpNo;

    private String billType;

    private BigDecimal issuedQty;

    private Collection<String> statusColl;

    private Collection<String> itemKeyColl;

    private Collection<String> mtrLotNoColl;

    public static WipMtrRtcQueryVO buildForMoUnPost(String organizationId, String moId, String headerId, String billType, String wkpNo, Collection<String> itemKeyColl) {
        WipMtrRtcQueryVO rtcLineQueryVO = new WipMtrRtcQueryVO();
        rtcLineQueryVO.setOrganizationId(organizationId)
                .setMoId(moId)
                .setHeaderId(headerId)
                .setBillType(billType)
                .setWkpNo(wkpNo)
                .setItemKeyColl(itemKeyColl)
                // 未过账状态
                .setStatusColl(WipMtrRtcLineStatusEnum.getUnPostStatus());
        return rtcLineQueryVO;
    }

    public static WipMtrRtcQueryVO buildForItemUnPost(String organizationId, String factoryId, String headerId, String billType, Collection<String> itemKeyColl) {
        WipMtrRtcQueryVO rtcLineQueryVO = new WipMtrRtcQueryVO();
        rtcLineQueryVO.setOrganizationId(organizationId)
                .setFactoryId(factoryId)
                .setHeaderId(headerId)
                .setBillType(billType)
                .setItemKeyColl(itemKeyColl)
                // 未过账状态
                .setStatusColl(WipMtrRtcLineStatusEnum.getUnPostStatus());
        return rtcLineQueryVO;
    }

}
