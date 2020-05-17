package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqPrintLogEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqPrintLogRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqPrintLogMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqPrintLogDO;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 16:47
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqPrintLogRepositoryImpl implements WipReqPrintLogRepository {

    private WipReqPrintLogMapper wipReqPrintLogMapper;

    public WipReqPrintLogRepositoryImpl(WipReqPrintLogMapper wipReqPrintLogMapper) {
        this.wipReqPrintLogMapper = wipReqPrintLogMapper;
    }

    @Override
    public void insert(WipReqPrintLogEntity printLogEntity) {
        WipReqPrintLogDO insertDO = WipReqPrintLogDO.buildDO(printLogEntity);
        wipReqPrintLogMapper.insertSelective(insertDO);
    }

}
