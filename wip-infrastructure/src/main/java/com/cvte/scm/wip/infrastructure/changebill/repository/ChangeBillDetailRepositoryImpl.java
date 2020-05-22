package com.cvte.scm.wip.infrastructure.changebill.repository;

import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillDetailRepository;
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
public class ChangeBillDetailRepositoryImpl implements ChangeBillDetailRepository {

    @Override
    public List<ChangeBillDetailEntity> selectByBillId(String billId) {
        return null;
    }

    @Override
    public void insert(ChangeBillDetailEntity entity) {

    }

    @Override
    public void update(ChangeBillDetailEntity entity) {

    }
}
