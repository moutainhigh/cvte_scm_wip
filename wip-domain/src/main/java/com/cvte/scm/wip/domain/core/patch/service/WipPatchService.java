package com.cvte.scm.wip.domain.core.patch.service;

import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;

/**
 * @version 1.0
 * @descriptions: 投料单头信息处理服务类
 * @author: ykccchen
 * @date: 2020/7/24 4:29 下午
 */
public interface WipPatchService {

    String insert(WipPatchEntity wipPatchEntity);

    int update(WipPatchEntity wipPatchEntity,String status);


}
