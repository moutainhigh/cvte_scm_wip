package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 16:41
 */
public interface MultiOrgSwitchService {

    OrgRelationBaseEntity getParentOrgRelationByType(String orgRelationId, String orgType);

}
