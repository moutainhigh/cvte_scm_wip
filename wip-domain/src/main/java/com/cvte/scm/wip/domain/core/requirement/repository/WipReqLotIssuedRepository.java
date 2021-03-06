package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 15:50
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReqLotIssuedRepository extends WipBaseRepository<WipReqLotIssuedEntity> {

    int selectCnBillTypeLot(String organizationId, String headerId, String itemKey);

    List<WipReqLotIssuedEntity> selectByKey(String organizationId, String moId, String itemNo, String wkpNo);

}
