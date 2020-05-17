package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.scm.wip.domain.common.user.entity.OrgExtEntity;
import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 15:46
 */
public interface OrgService {

    OrgRelationBaseEntity selectOrgRelationById(String orgRelationId);

    OrgExtEntity getOrgExtById(String id);

}
