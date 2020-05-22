package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.base.WipBaseService;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcInoutStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-05-16
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMcInoutStockService extends WipBaseService<WipMcInoutStockEntity, WipMcInoutStockRepository> {


    public WipMcInoutStockEntity getById(String inoutStockId) {

        WipMcInoutStockEntity wipMcInoutStock = repository.selectOne(
                new WipMcInoutStockEntity().setInoutStockId(inoutStockId));
        if (ObjectUtils.isNull(wipMcInoutStock)) {
            throw new SourceNotFoundException("获取调拨单数据错误");
        }

        return wipMcInoutStock;

    }

}
