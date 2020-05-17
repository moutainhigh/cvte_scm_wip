package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 15:50
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReqLotIssuedRepository {

    List<WipReqLotIssuedEntity> selectList(WipReqLotIssuedEntity lotIssuedEntity);

    WipReqLotIssuedEntity selectById(String id);

    void insert(WipReqLotIssuedEntity lotIssuedEntity);

    void invalidById(List<String> idList);

}
