package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotProcessRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqLotProcMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLotProcDO;
import org.springframework.stereotype.Repository;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/4 10:46
 */
@Repository
public class WipReqLotProcessRepositoryImpl
        extends WipBaseRepositoryImpl<WipReqLotProcMapper, WipReqLotProcDO, WipReqLotProcessEntity>
        implements WipReqLotProcessRepository {

    @Override
    protected Class<WipReqLotProcessEntity> getEntityClass() {
        return WipReqLotProcessEntity.class;
    }

    @Override
    protected Class<WipReqLotProcDO> getDomainClass() {
        return WipReqLotProcDO.class;
    }

}
