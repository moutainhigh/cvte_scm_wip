package com.cvte.scm.wip.domain.core.item.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.item.entity.ScmItemOrgEntity;

import java.util.Collection;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/19 11:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ScmItemOrgRepository extends WipBaseRepository<ScmItemOrgEntity> {

    List<ScmItemOrgEntity> selectByIds(String organizationId, Collection<String> itemIds);

}
