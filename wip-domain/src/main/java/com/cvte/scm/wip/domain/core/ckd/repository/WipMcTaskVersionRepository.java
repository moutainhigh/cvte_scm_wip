package com.cvte.scm.wip.domain.core.ckd.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskVersionEntity;

import java.util.List;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-28
 */
public interface WipMcTaskVersionRepository extends WipBaseRepository<WipMcTaskVersionEntity> {

    WipMcTaskVersionEntity getLastVersion(String taskId);

    List<WipMcTaskVersionEntity> listWipMcTaskVersion(String taskId);

}