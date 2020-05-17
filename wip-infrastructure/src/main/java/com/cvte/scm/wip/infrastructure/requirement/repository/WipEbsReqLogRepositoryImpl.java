package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipEbsReqLogEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipEbsReqLogRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipEbsReqLogMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipEbsReqLogDO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 21:59
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipEbsReqLogRepositoryImpl implements WipEbsReqLogRepository {

    private WipEbsReqLogMapper wipEbsReqLogMapper;

    public WipEbsReqLogRepositoryImpl(WipEbsReqLogMapper wipEbsReqLogMapper) {
        this.wipEbsReqLogMapper = wipEbsReqLogMapper;
    }

    @Override
    public List<WipEbsReqLogEntity> selectBetweenTimeInStatus(Date timeFrom, Date timeTo, String... processStatus) {
        List<WipEbsReqLogDO> ebsReqLogDOList = wipEbsReqLogMapper.selectBetweenTimeInStatus(timeFrom, timeTo, processStatus);
        return WipEbsReqLogDO.batchBuildEntity(ebsReqLogDOList);
    }
}
