package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 10:46
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReqLotProcessRepository extends WipBaseRepository<WipReqLotProcessEntity> {

    List<WipReqLotProcessEntity> selectNeedProcess();

}
