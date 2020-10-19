package com.cvte.scm.wip.app.rtc.status;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcWriteBackService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/21 10:29
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcLineCancelApplication {

    private WipMtrRtcWriteBackService wipMtrRtcWriteBackService;

    public WipMtrRtcLineCancelApplication(WipMtrRtcWriteBackService wipMtrRtcWriteBackService) {
        this.wipMtrRtcWriteBackService = wipMtrRtcWriteBackService;
    }

    public void doAction(String[] lineIds) {
        List<WipMtrRtcLineEntity> rtcLineList = WipMtrRtcLineEntity.get().getByLineIds(lineIds);
        WipMtrRtcHeaderEntity rtcHeader = WipMtrRtcHeaderEntity.get().getById(rtcLineList.get(0).getHeaderId());
        rtcHeader.checkCancelable();
        if (WipMtrRtcHeaderStatusEnum.effective(rtcHeader.getBillStatus())) {
            rtcHeader.setLineList(rtcLineList);
            wipMtrRtcWriteBackService.cancelLine(rtcHeader);
        }
        WipMtrRtcLineEntity.get().batchCancel(rtcLineList);
    }

}
