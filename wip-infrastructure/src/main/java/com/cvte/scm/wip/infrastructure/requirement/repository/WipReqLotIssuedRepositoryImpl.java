package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqLotIssuedMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLotIssuedDO;
import org.springframework.stereotype.Repository;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/5/17 15:59
 */
@Repository
public class WipReqLotIssuedRepositoryImpl
        extends WipBaseRepositoryImpl<WipReqLotIssuedMapper, WipReqLotIssuedDO, WipReqLotIssuedEntity>
        implements WipReqLotIssuedRepository {

}
