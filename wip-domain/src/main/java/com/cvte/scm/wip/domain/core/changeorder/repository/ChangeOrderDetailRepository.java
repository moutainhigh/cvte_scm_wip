package com.cvte.scm.wip.domain.core.changeorder.repository;

import com.cvte.scm.wip.domain.core.changeorder.entity.ChangeOrderDetailEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 15:29
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ChangeOrderDetailRepository {

    List<ChangeOrderDetailEntity> selectByBillId(String billId);

    void insert(ChangeOrderDetailEntity entity);

}
