package com.cvte.scm.wip.domain.core.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 11:59
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipMtrSubInvRepository {

    List<WipMtrSubInvVO> selectByVO(List<WipMtrSubInvVO> subInvVOList);

}
