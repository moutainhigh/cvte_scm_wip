package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/29 10:57
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipMtrRtcWriteBackService {

    String createAndSubmit(WipMtrRtcHeaderEntity rtcHeader);

    String update(WipMtrRtcHeaderEntity rtcHeader);

    void syncLineInfo(WipMtrRtcHeaderEntity rtcHeaderEntity);

    String cancel(WipMtrRtcHeaderEntity rtcHeader);

    String close(WipMtrRtcHeaderEntity rtcHeader);

    String post(WipMtrRtcHeaderEntity rtcHeader);

    String cancelLine(WipMtrRtcHeaderEntity rtcHeader);

}
