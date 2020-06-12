package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockLineEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcInoutStockLineRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcInoutStockLineMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcInoutStockLineDO;
import org.springframework.stereotype.Repository;

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
    protected Class<WipMcInoutStockLineEntity> getEntityClass() {
        return WipMcInoutStockLineEntity.class;
    }

    @Override
    protected Class<WipMcInoutStockLineDO> getDomainClass() {
        return WipMcInoutStockLineDO.class;
    }
}