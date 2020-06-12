package com.cvte.scm.wip.domain.core.ckd.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineEntity;

import java.util.List;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-28
 */
public interface WipMcTaskLineRepository extends WipBaseRepository<WipMcTaskLineEntity> {


    List<WipMcTaskLineView> listWipMcTaskLineView(WipMcTaskLineQuery query);

    List<WipMcTaskLineEntity> listWipMcTaskLine(WipMcTaskLineQuery query);
}