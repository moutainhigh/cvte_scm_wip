package com.cvte.scm.wip.domain.core.ckd.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockEntity;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-05-18
 */
public interface WipMcInoutStockRepository extends WipBaseRepository<WipMcInoutStockEntity> {

    WipMcInoutStockEntity selectOne(WipMcInoutStockEntity wipMcInoutStockEntity);
}