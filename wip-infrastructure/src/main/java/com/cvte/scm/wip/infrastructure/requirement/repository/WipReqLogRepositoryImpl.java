package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLogEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLogRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqLogMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLogDO;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 16:37
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqLogRepositoryImpl implements WipReqLogRepository {

    private WipReqLogMapper wipReqLogMapper;

    public WipReqLogRepositoryImpl(WipReqLogMapper wipReqLogMapper) {
        this.wipReqLogMapper = wipReqLogMapper;
    }

    @Override
    public void insert(WipReqLogEntity logEntity) {
        WipReqLogDO insertDO = WipReqLogDO.buildDO(logEntity);
        wipReqLogMapper.insertSelective(insertDO);
    }
}
