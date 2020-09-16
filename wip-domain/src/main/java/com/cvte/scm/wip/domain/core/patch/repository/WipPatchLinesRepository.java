package com.cvte.scm.wip.domain.core.patch.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchLinesEntity;

import java.util.List;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 8:41 下午
 */
public interface WipPatchLinesRepository extends WipBaseRepository<WipPatchLinesEntity> {

    List<WipPatchLinesEntity> selectListByBillId(String billId, String itemId);
}
