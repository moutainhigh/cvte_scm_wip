package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.scm.wip.domain.common.base.WipBaseService;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockLineEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcInoutStockLineRepository;
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
public class WipMcInoutStockLineService extends WipBaseService<WipMcInoutStockLineEntity, WipMcInoutStockLineRepository>  {

}
