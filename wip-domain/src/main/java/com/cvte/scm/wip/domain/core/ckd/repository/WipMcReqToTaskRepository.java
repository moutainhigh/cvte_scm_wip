package com.cvte.scm.wip.domain.core.ckd.repository;

import com.cvte.scm.wip.domain.core.ckd.entity.WipMcReqToTaskEntity;

import java.util.List;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-28
 */
public interface WipMcReqToTaskRepository {

    List<WipMcReqToTaskEntity> selectList(WipMcReqToTaskEntity wipMcReqToTaskEntity);

    void insertList(List<WipMcReqToTaskEntity> list);
}