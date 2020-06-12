package com.cvte.scm.wip.domain.core.ckd.repository;


import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineVersionQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineVersionView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineVersionEntity;

import java.util.List;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-28
 */
public interface WipMcTaskLineVersionRepository extends WipBaseRepository<WipMcTaskLineVersionEntity> {


    List<WipMcTaskLineVersionView> listWipMcTaskLineVersionView(WipMcTaskLineVersionQuery query);
}