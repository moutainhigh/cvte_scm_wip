package com.cvte.scm.wip.domain.core.patch.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchHistoryEntity;

import java.util.List;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 8:40 下午
 */
public interface WipPatchHistoryRepository extends WipBaseRepository<WipPatchHistoryEntity> {

    List<WipPatchHistoryEntity> selectById(String billId);


}
