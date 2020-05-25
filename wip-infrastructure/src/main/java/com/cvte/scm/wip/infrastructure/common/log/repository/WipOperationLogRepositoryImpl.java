package com.cvte.scm.wip.infrastructure.common.log.repository;

import com.cvte.scm.wip.domain.common.log.entity.WipOperationLogEntity;
import com.cvte.scm.wip.domain.common.log.repository.WipOperationLogRepository;
import com.cvte.scm.wip.infrastructure.common.log.mapper.WipOperationLogMapper;
import com.cvte.scm.wip.infrastructure.common.log.mapper.dataobject.WipOperationLogDO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author zy
 * @date 2020-05-25 09:36
 **/
@Repository
public class WipOperationLogRepositoryImpl implements WipOperationLogRepository {

    @Autowired
    private WipOperationLogMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void insertSelective(WipOperationLogEntity entity) {
        mapper.insertSelective(modelMapper.map(entity, WipOperationLogDO.class));
    }
}
