package com.cvte.scm.wip.domain.core.rtc.valueobject;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/8 16:16
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrRtcLineQueryVO {

    private String lineId;

    private String moId;

    private String headerId;

    private String organizationId;

    private String itemId;

    private String itemNo;

    private String moLotNo;

    private String wkpNo;

    private String invpNo;

    private String billType;

    private Collection<String> statusColl;

    public static WipMtrRtcLineQueryVO buildForPosted(WipMtrRtcHeaderEntity rtcHeaderEntity, WipMtrRtcLineEntity rtcLineEntity) {
        WipMtrRtcLineQueryVO rtcLineQueryVO = new WipMtrRtcLineQueryVO();
        rtcLineQueryVO.setOrganizationId(rtcHeaderEntity.getOrganizationId())
                .setMoId(rtcHeaderEntity.getMoId())
                .setHeaderId(rtcHeaderEntity.getHeaderId())
                .setWkpNo(rtcLineEntity.getWkpNo())
                .setItemId(rtcLineEntity.getItemId())
                // 未过账状态
                .setStatusColl(WipMtrRtcLineStatusEnum.getUnPostStatus());
        return rtcLineQueryVO;
    }

    public static WipMtrRtcLineQueryVO buildForPosted(WipMtrRtcHeaderBuildVO rtcHeaderBuildVO, WipMtrRtcLineEntity rtcLineEntity) {
        WipMtrRtcLineQueryVO rtcLineQueryVO = new WipMtrRtcLineQueryVO();
        rtcLineQueryVO.setOrganizationId(rtcHeaderBuildVO.getOrganizationId())
                .setMoId(rtcHeaderBuildVO.getMoId())
                .setHeaderId(rtcHeaderBuildVO.getHeaderId())
                .setBillType(rtcHeaderBuildVO.getBillType())
                .setWkpNo(rtcLineEntity.getWkpNo())
                .setItemId(rtcLineEntity.getItemId())
                // 未过账状态
                .setStatusColl(WipMtrRtcLineStatusEnum.getUnPostStatus());
        return rtcLineQueryVO;
    }

}
