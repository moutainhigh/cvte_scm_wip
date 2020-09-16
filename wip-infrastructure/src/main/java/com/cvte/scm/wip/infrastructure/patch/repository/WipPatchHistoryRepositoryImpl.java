package com.cvte.scm.wip.infrastructure.patch.repository;

import com.cvte.scm.wip.domain.core.patch.entity.WipPatchHistoryEntity;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchHistoryRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.patch.mapper.WipPatchHistoryMapper;
import com.cvte.scm.wip.infrastructure.patch.mapper.dataobject.WipPatchHistoryDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 8:40 下午
 */
@Repository
public class WipPatchHistoryRepositoryImpl
        extends WipBaseRepositoryImpl<WipPatchHistoryMapper, WipPatchHistoryDO, WipPatchHistoryEntity>
        implements WipPatchHistoryRepository {


    WipPatchHistoryMapper wipPatchHistoryMapper;

    public WipPatchHistoryRepositoryImpl(WipPatchHistoryMapper wipPatchHistoryMapper) {
        this.wipPatchHistoryMapper = wipPatchHistoryMapper;
    }



    @Override
    protected Class<WipPatchHistoryEntity> getEntityClass() {
        return WipPatchHistoryEntity.class;
    }

    @Override
    protected Class<WipPatchHistoryDO> getDomainClass() {
        return WipPatchHistoryDO.class;
    }

    @Override
    public  List<WipPatchHistoryEntity> selectById(String billId) {
        List<WipPatchHistoryDO> select = wipPatchHistoryMapper.select(new WipPatchHistoryDO().setBillId(billId));
        return batchBuildEntity(select);
    }
}
