package com.cvte.scm.wip.app.rtc.status;

import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcWriteBackService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
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
public class WipMtrRtcHeaderReviewApplication {

    private WipMtrRtcWriteBackService wipMtrRtcWriteBackService;

    public WipMtrRtcHeaderReviewApplication(WipMtrRtcWriteBackService wipMtrRtcWriteBackService) {
        this.wipMtrRtcWriteBackService = wipMtrRtcWriteBackService;
    }

    public void doAction(WipMtrRtcHeaderReviewDTO rtcHeaderReviewDTO) {
        WipMtrRtcHeaderEntity rtcHeader = WipMtrRtcHeaderEntity.get().getById(rtcHeaderReviewDTO.getHeaderId());
        // 审核
        rtcHeader.review(rtcHeaderReviewDTO.getApproved());
        if (BooleanEnum.YES.getCode().equals(rtcHeaderReviewDTO.getApproved())) {
            // 同步到EBS
            List<WipMtrRtcLineEntity> rtcLineList = rtcHeader.getLineList();
            rtcLineList.removeIf(line -> WipMtrRtcLineStatusEnum.CANCELED.getCode().equals(line.getLineStatus()));
            wipMtrRtcWriteBackService.sync(rtcHeader);
            rtcHeader.update();
            rtcHeader.saveLines(false);
        }
    }

}
