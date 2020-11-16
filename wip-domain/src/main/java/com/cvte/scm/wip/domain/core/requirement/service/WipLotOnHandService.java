package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;

import java.util.Collection;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/10/13 10:48
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipLotOnHandService {

    List<WipMtrSubInvVO> getOnHand(String organizationId, String itemId, Collection<String> lotNumbers);

}
