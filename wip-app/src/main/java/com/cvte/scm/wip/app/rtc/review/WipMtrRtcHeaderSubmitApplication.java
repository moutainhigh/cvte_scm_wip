package com.cvte.scm.wip.app.rtc.review;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcLineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 15:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcHeaderSubmitApplication {

    private WipMtrRtcLineService wipMtrRtcLineService;

    public WipMtrRtcHeaderSubmitApplication(WipMtrRtcLineService wipMtrRtcLineService) {
        this.wipMtrRtcLineService = wipMtrRtcLineService;
    }

    public void doAction(String headerId) {
        WipMtrRtcHeaderEntity rtcHeaderEntity = WipMtrRtcHeaderEntity.get().getById(headerId);
        List<WipMtrRtcLineEntity> rtcLineEntityList = rtcHeaderEntity.getLineList();
        wipMtrRtcLineService.validateItemInvQty(rtcLineEntityList);

        rtcHeaderEntity.submit();
    }

}
