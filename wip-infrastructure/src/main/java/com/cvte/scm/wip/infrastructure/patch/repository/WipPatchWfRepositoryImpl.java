package com.cvte.scm.wip.infrastructure.patch.repository;

import com.cvte.scm.wip.domain.core.patch.entity.WipPatchWfEntity;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchWfRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.patch.mapper.WipPatchWfMapper;
import com.cvte.scm.wip.infrastructure.patch.mapper.dataobject.WipPatchWfDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 8:40 下午
 */
@Repository
public class WipPatchWfRepositoryImpl
        extends WipBaseRepositoryImpl<WipPatchWfMapper, WipPatchWfDO, WipPatchWfEntity>
        implements WipPatchWfRepository {


    WipPatchWfMapper wipPatchWfMapper;

    public WipPatchWfRepositoryImpl(WipPatchWfMapper wipPatchWfMapper) {
        this.wipPatchWfMapper = wipPatchWfMapper;
    }

    @Override
    protected Class<WipPatchWfEntity> getEntityClass() {
        return WipPatchWfEntity.class;
    }

    @Override
    protected Class<WipPatchWfDO> getDomainClass() {
        return WipPatchWfDO.class;
    }


    @Override
    public List<WipPatchWfEntity> selectById(String billId) {
        List<WipPatchWfDO> select = wipPatchWfMapper.select(new WipPatchWfDO().setBillId(billId));
        return batchBuildEntity(select);
    }

    @Override
    public Integer insertBatchReturnId(WipPatchWfEntity wipPatchWfEntity) {
        WipPatchWfDO wipPatchWfDO = buildDO(wipPatchWfEntity);
        wipPatchWfMapper.insertBatchReturnId(wipPatchWfDO);
        return wipPatchWfDO.getId();
    }
}
