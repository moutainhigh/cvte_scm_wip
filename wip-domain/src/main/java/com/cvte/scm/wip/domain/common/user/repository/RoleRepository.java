package com.cvte.scm.wip.domain.common.user.repository;

import com.cvte.scm.wip.domain.common.user.entity.RoleEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:41
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface RoleRepository {

    List<RoleEntity> listRoleByUserId(String userId);

}
