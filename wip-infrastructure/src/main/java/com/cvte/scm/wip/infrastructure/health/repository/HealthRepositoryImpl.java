package com.cvte.scm.wip.infrastructure.health.repository;

import com.cvte.scm.wip.domain.common.health.repository.HealthRepository;
import com.cvte.scm.wip.infrastructure.health.dal.HealthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:02
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class HealthRepositoryImpl implements HealthRepository {

    @Autowired
    private HealthMapper healthMapper;

    @Override
    public String getMessage() {
        return healthMapper.getMessage();
    }

}
