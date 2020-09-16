package com.cvte.scm.wip.domain.core.patch.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 8:40 下午
 */
public interface WipPatchRepository extends WipBaseRepository<WipPatchEntity> {

    WipPatchEntity selectById(String billId);


}
