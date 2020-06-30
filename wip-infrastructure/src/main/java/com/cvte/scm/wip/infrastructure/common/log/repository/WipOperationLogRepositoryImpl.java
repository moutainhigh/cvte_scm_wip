package com.cvte.scm.wip.infrastructure.common.log.repository;

import com.cvte.scm.wip.domain.common.log.entity.WipOperationLogEntity;
import com.cvte.scm.wip.domain.common.log.repository.WipOperationLogRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.common.log.mapper.WipOperationLogMapper;
import com.cvte.scm.wip.infrastructure.common.log.mapper.dataobject.WipOperationLogDO;
import org.springframework.stereotype.Repository;

/**
 * @author zy
 * @date 2020-05-25 09:36
 **/
@Repository
public class WipOperationLogRepositoryImpl
        extends WipBaseRepositoryImpl<WipOperationLogMapper, WipOperationLogDO, WipOperationLogEntity>
        implements WipOperationLogRepository {

    @Override
    protected Class<WipOperationLogEntity> getEntityClass() {
        return WipOperationLogEntity.class;
    }

    @Override
    protected Class<WipOperationLogDO> getDomainClass() {
        return WipOperationLogDO.class;
    }
}
