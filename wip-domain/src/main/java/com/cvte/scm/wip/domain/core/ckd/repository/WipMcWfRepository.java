package com.cvte.scm.wip.domain.core.ckd.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcWfQuery;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcWfEntity;

import java.util.List;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-29
 */
public interface WipMcWfRepository extends WipBaseRepository<WipMcWfEntity> {

    List<WipMcWfEntity> listWipMcWf(WipMcWfQuery query);

}