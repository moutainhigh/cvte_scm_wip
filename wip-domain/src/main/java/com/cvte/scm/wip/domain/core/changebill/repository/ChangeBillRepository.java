package com.cvte.scm.wip.domain.core.changebill.repository;

import com.cvte.scm.wip.common.base.domain.Repository;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 14:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ChangeBillRepository extends Repository {

    void insert(ChangeBillEntity entity);

    void update(ChangeBillEntity entity);

    ChangeBillEntity getByKey(String billKey);

    List<ChangeBillEntity> getById(List<String> billIdList);

    ChangeBillEntity getByReqInsHeaderId(String reqInsHeaderId);

    List<ChangeBillEntity> getSyncFailedBills(List<String> errMsgList);

}
