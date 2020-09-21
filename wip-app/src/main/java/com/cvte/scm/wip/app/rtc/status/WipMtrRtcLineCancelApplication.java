package com.cvte.scm.wip.app.rtc.status;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
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

    public void doAction(String[] lineIds) {
        List<WipMtrRtcLineEntity> rtcLineEntityList = WipMtrRtcLineEntity.get().getByLineIds(lineIds);
        WipMtrRtcLineEntity.get().batchCancel(rtcLineEntityList);
    }

}
