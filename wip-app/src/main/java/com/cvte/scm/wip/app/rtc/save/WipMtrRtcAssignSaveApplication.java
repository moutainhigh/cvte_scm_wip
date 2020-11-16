package com.cvte.scm.wip.app.rtc.save;

import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcAssignService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcAssignBuildVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/11 09:53
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcAssignSaveApplication {

    private WipMtrRtcAssignService wipMtrRtcAssignService;

    public WipMtrRtcAssignSaveApplication(WipMtrRtcAssignService wipMtrRtcAssignService) {
        this.wipMtrRtcAssignService = wipMtrRtcAssignService;
    }

    public String doAction(List<WipMtrRtcAssignBuildVO> rtcAssignBuildVOList) {
        return wipMtrRtcAssignService.batchSave(rtcAssignBuildVOList);
    }

}
