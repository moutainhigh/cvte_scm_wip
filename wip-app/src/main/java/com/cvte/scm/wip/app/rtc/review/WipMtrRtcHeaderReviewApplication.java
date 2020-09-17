package com.cvte.scm.wip.app.rtc.review;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 15:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcHeaderReviewApplication {

    public void doAction(WipMtrRtcHeaderReviewDTO rtcHeaderReviewDTO) {
        WipMtrRtcHeaderEntity rtcHeaderEntity = WipMtrRtcHeaderEntity.get().getById(rtcHeaderReviewDTO.getHeaderId());
        rtcHeaderEntity.review(rtcHeaderReviewDTO.getApproved());
    }

}
