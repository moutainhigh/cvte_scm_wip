package com.cvte.scm.wip.app.rtc.refresh;

import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcHeaderService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/9 18:59
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcRefreshApplication {

    private WipMtrRtcHeaderService wipMtrRtcHeaderService;

    public WipMtrRtcRefreshApplication(WipMtrRtcHeaderService wipMtrRtcHeaderService) {
        this.wipMtrRtcHeaderService = wipMtrRtcHeaderService;
    }

    public String doAction(WipMtrRtcHeaderBuildVO rtcHeaderBuildVO) {
        return wipMtrRtcHeaderService.refresh(rtcHeaderBuildVO);
    }

}
