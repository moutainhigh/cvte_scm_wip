package com.cvte.scm.wip.domain.core.changebill.repository;

import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 15:29
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ChangeBillDetailRepository {

    void insert(ChangeBillDetailEntity entity);

    void update(ChangeBillDetailEntity entity);

    List<ChangeBillDetailEntity> selectByBillId(String billId);

}
