package com.cvte.scm.wip.app.rtc.update;

import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcLineService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/10 19:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcLineUpdateApplication {

    private WipMtrRtcLineService wipMtrRtcLineService;

    public WipMtrRtcLineUpdateApplication(WipMtrRtcLineService wipMtrRtcLineService) {
        this.wipMtrRtcLineService = wipMtrRtcLineService;
    }

    public String doAction(List<WipMtrRtcLineBuildVO> rtcLineBuildVOList) {
        return wipMtrRtcLineService.batchUpdate(rtcLineBuildVOList);
    }

}
