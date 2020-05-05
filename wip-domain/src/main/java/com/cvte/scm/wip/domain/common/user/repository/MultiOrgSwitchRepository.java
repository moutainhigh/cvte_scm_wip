package com.cvte.scm.wip.domain.common.user.repository;

import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:27
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface MultiOrgSwitchRepository {

    OrgRelationBaseEntity getParentOrgRelationByType(String orgRelationId, String orgType);

}
