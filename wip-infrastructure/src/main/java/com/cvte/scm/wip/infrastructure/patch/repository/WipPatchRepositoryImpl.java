package com.cvte.scm.wip.infrastructure.patch.repository;

import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.patch.mapper.WipPatchMapper;
import com.cvte.scm.wip.infrastructure.patch.mapper.dataobject.WipPatchDO;
import org.springframework.stereotype.Repository;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 8:40 下午
 */
@Repository
public class WipPatchRepositoryImpl
        extends WipBaseRepositoryImpl<WipPatchMapper, WipPatchDO, WipPatchEntity>
        implements WipPatchRepository {


    WipPatchMapper wipPatchMapper;

    public WipPatchRepositoryImpl(WipPatchMapper wipPatchMapper) {
        this.wipPatchMapper = wipPatchMapper;
    }

    @Override
    protected Class<WipPatchEntity> getEntityClass() {
        return WipPatchEntity.class;
    }

    @Override
    protected Class<WipPatchDO> getDomainClass() {
        return WipPatchDO.class;
    }

    @Override
    public WipPatchEntity selectById(String billId) {
        return buildEntity(wipPatchMapper.selectByPrimaryKey(billId));
    }
}
