package com.cvte.scm.wip.domain.common.log.repository;

import com.cvte.scm.wip.domain.common.log.entity.WipOperationLogEntity;

/**
 * @author zy
 * @date 2020-05-25 09:33
 **/
public interface WipOperationLogRepository {

    void insertSelective(WipOperationLogEntity entity);
}
