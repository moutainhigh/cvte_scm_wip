package com.cvte.scm.wip.infrastructure.patch.repository;

import com.cvte.scm.wip.domain.core.patch.entity.WipPatchLinesEntity;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchLinesRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.patch.mapper.WipPatchLinesMapper;
import com.cvte.scm.wip.infrastructure.patch.mapper.dataobject.WipPatchLinesDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 8:41 下午
 */
@Repository
public class WipPatchLinesRepositoryImpl
        extends WipBaseRepositoryImpl<WipPatchLinesMapper, WipPatchLinesDO, WipPatchLinesEntity>
        implements WipPatchLinesRepository {


    WipPatchLinesMapper wipPatchLinesMapper;

    public WipPatchLinesRepositoryImpl(WipPatchLinesMapper wipPatchLinesMapper) {
        this.wipPatchLinesMapper = wipPatchLinesMapper;
    }

    @Override
    protected Class<WipPatchLinesEntity> getEntityClass() {
        return WipPatchLinesEntity.class;
    }

    @Override
    protected Class<WipPatchLinesDO> getDomainClass() {
        return WipPatchLinesDO.class;
    }

    @Override
    public List<WipPatchLinesEntity> selectListByBillId(String billId,String itemId) {
        return batchBuildEntity(wipPatchLinesMapper.select(new WipPatchLinesDO().setBillId(billId).setItemId(itemId)));
    }
}
