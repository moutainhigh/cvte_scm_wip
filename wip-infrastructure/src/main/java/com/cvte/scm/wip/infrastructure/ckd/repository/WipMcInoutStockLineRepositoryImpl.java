package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockLineEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcInoutStockLineRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcInoutStockLineMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcInoutStockLineDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author zy
 * @since 2020-05-18
 */
@Repository
public class WipMcInoutStockLineRepositoryImpl
        extends WipBaseRepositoryImpl<WipMcInoutStockLineMapper, WipMcInoutStockLineDO, WipMcInoutStockLineEntity>
        implements WipMcInoutStockLineRepository {

    @Override
    protected List<WipMcInoutStockLineDO> batchBuildDO(List<WipMcInoutStockLineEntity> entityList) {
        return null;
    }

    @Override
    protected WipMcInoutStockLineDO buildDO(WipMcInoutStockLineEntity entity) {
        return null;
    }

    @Override
    protected WipMcInoutStockLineEntity buildEntity(WipMcInoutStockLineDO domain) {
        return null;
    }

    @Override
    protected List<WipMcInoutStockLineEntity> batchBuildEntity(List<WipMcInoutStockLineDO> entityList) {
        return null;
    }
}