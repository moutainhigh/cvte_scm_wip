package com.cvte.scm.wip.domain.common.user.repository;

import com.cvte.scm.wip.domain.common.user.entity.OrgExtEntity;
import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:31
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface OrgRepository {

    OrgRelationBaseEntity getOrgRelationById(String orgRelationId);

    OrgExtEntity getOrgExtById(String id);

}
