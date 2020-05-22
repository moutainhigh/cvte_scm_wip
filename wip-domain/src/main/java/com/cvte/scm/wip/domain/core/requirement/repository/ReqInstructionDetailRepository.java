package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionDetailEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:52
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ReqInstructionDetailRepository {

    void insert(ReqInstructionDetailEntity detailEntity);

    void update(ReqInstructionDetailEntity detailEntity);

    List<ReqInstructionDetailEntity> getByInsId(String id);

}
