package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqMtrsEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqMtrsRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqMtrsMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqMtrsDO;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 16:55
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqMtrsRepositoryImpl implements WipReqMtrsRepository {

    private WipReqMtrsMapper wipReqMtrsMapper;

    public WipReqMtrsRepositoryImpl(WipReqMtrsMapper wipReqMtrsMapper) {
        this.wipReqMtrsMapper = wipReqMtrsMapper;
    }

    @Override
    public Integer selectCount(WipReqMtrsEntity mtrsEntity) {
        WipReqMtrsDO mtrsDO = WipReqMtrsDO.buildDO(mtrsEntity);
        return wipReqMtrsMapper.selectCount(mtrsDO);
    }
}