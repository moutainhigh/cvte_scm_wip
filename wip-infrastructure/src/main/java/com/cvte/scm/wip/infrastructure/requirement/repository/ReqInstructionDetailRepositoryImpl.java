package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionDetailRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ReqInstructionDetailRepositoryImpl implements ReqInstructionDetailRepository {

    @Override
    public void insert(ReqInstructionDetailEntity detailEntity) {

    }

    @Override
    public void update(ReqInstructionDetailEntity detailEntity) {

    }

    @Override
    public List<ReqInstructionDetailEntity> getByInsId(String id) {
        return null;
    }

}
