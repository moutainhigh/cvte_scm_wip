package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcInoutStockRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcInoutStockMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcInoutStockDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author zy
 * @since 2020-05-18
 */
@Repository
public class WipMcInoutStockRepositoryImpl
        extends WipBaseRepositoryImpl<WipMcInoutStockMapper, WipMcInoutStockDO, WipMcInoutStockEntity>
        implements WipMcInoutStockRepository {

    @Autowired
    private WipMcInoutStockMapper wipMcInoutStockMapper;

    @Override
    protected Class<WipMcInoutStockEntity> getEntityClass() {
        return WipMcInoutStockEntity.class;
    }

    @Override
    protected Class<WipMcInoutStockDO> getDomainClass() {
        return WipMcInoutStockDO.class;
    }

    @Override
    public WipMcInoutStockEntity selectOne(WipMcInoutStockEntity wipMcInoutStockEntity) {
        return buildEntity(wipMcInoutStockMapper.selectOne(buildDO(wipMcInoutStockEntity)));
    }
}