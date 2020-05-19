package com.cvte.scm.wip.domain.core.changeorder.repository;

import com.cvte.scm.wip.common.base.domain.Repository;
import com.cvte.scm.wip.domain.core.changeorder.entity.ChangeOrderEntity;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 14:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ChangeOrderRepository extends Repository {

    void insert(ChangeOrderEntity entity);

}
