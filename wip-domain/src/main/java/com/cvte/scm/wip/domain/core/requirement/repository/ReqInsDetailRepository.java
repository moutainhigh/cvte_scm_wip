package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:52
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ReqInsDetailRepository {

    void insert(ReqInsDetailEntity entity);

    void update(ReqInsDetailEntity entity);

    List<ReqInsDetailEntity> getByInsId(String insId);

}
