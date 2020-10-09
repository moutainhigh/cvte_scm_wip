package com.cvte.scm.wip.app.rtc.status;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcWriteBackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/21 09:39
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcHeaderCancelApplication {

    private WipMtrRtcWriteBackService wipMtrRtcWriteBackService;

    public WipMtrRtcHeaderCancelApplication(WipMtrRtcWriteBackService wipMtrRtcWriteBackService) {
        this.wipMtrRtcWriteBackService = wipMtrRtcWriteBackService;
    }

    public void doAction(String headerId) {
        WipMtrRtcHeaderEntity rtcHeader = WipMtrRtcHeaderEntity.get().getById(headerId);
        rtcHeader.cancel();
        wipMtrRtcWriteBackService.cancel(rtcHeader);
    }

}
