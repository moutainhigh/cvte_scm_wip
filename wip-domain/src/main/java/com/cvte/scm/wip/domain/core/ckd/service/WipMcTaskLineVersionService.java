package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.scm.wip.domain.common.base.WipBaseService;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineVersionQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineVersionView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineVersionEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcTaskLineVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-04-28
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMcTaskLineVersionService  extends WipBaseService<WipMcTaskLineVersionEntity, WipMcTaskLineVersionRepository> {

    public List<WipMcTaskLineVersionView> listWipMcTaskLineVersionView(WipMcTaskLineVersionQuery query) {
        return repository.listWipMcTaskLineVersionView(query);
    }

}
