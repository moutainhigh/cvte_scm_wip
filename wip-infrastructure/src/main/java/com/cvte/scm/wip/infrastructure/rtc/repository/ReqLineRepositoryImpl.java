package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqLineRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqLinesMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLineDO;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/11/13 14:43
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ReqLineRepositoryImpl
        extends WipBaseRepositoryImpl<WipReqLinesMapper, WipReqLineDO, WipReqLineEntity>
        implements ReqLineRepository {
}
