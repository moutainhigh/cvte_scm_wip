package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionRepository;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:53
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ReqInstructionRepositoryImpl implements ReqInstructionRepository {

    @Override
    public void insert(ReqInstructionEntity InstructionEntity) {

    }

    @Override
    public void update(ReqInstructionEntity InstructionEntity) {

    }

    @Override
    public ReqInstructionEntity getById(String insId) {
        return null;
    }

}
