package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.scm.wip.domain.common.base.WipBaseService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 10:46
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipReqLotProcessService extends WipBaseService<WipReqLotProcessEntity, WipReqLotProcessRepository> {
}
