package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLotControlVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 16:45
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipMtrRtcLotControlService {

    List<WipMtrRtcLotControlVO> getLotControlByOptionNo(String optionNo, String optionValue);

}
