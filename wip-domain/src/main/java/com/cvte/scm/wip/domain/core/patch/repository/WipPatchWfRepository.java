package com.cvte.scm.wip.domain.core.patch.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchWfEntity;

import java.util.List;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 8:40 下午
 */
public interface WipPatchWfRepository extends WipBaseRepository<WipPatchWfEntity> {

    List<WipPatchWfEntity> selectById(String billId);

    Integer insertBatchReturnId(WipPatchWfEntity wipPatchWfEntity);


}
