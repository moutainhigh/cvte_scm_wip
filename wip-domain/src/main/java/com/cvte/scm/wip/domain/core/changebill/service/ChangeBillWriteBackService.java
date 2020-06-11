package com.cvte.scm.wip.domain.core.changebill.service;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/11 12:15
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ChangeBillWriteBackService {

    String writeBackToEbs(ReqInsEntity reqInsEntity);

}
