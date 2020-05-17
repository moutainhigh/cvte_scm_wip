package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqMtrsEntity;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 16:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReqMtrsRepository {

    Integer selectCount(WipReqMtrsEntity mtrsEntity);

}
