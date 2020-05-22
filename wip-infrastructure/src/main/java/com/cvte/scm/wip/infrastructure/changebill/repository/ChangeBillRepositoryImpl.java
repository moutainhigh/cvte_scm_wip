package com.cvte.scm.wip.infrastructure.changebill.repository;

import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 17:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ChangeBillRepositoryImpl implements ChangeBillRepository {
    @Override
    public void insert(ChangeBillEntity entity) {

    }

    @Override
    public void update(ChangeBillEntity entity) {

    }

    @Override
    public ChangeBillEntity getById(String billId) {
        return null;
    }
}
