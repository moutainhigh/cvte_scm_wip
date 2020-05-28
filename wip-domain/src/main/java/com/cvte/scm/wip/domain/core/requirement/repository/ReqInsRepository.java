package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ReqInsRepository {

    void insert(ReqInsEntity entity);

    void update(ReqInsEntity entity);

    ReqInsEntity getByKey(String insKey);

}
