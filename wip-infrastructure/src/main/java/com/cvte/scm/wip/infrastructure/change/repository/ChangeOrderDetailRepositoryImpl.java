package com.cvte.scm.wip.infrastructure.change.repository;

import com.cvte.scm.wip.domain.core.changeorder.entity.ChangeOrderDetailEntity;
import com.cvte.scm.wip.domain.core.changeorder.repository.ChangeOrderDetailRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 17:21
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ChangeOrderDetailRepositoryImpl implements ChangeOrderDetailRepository {
    @Override
    public List<ChangeOrderDetailEntity> selectByBillId(String billId) {
        return null;
    }

    @Override
    public void insert(ChangeOrderDetailEntity entity) {

    }
}
