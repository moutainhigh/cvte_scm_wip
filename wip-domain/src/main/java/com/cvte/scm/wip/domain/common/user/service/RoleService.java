package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.scm.wip.domain.common.user.entity.RoleEntity;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 09:43
 */
public interface RoleService {

    List<RoleEntity> listSysRoleByUserId(String userId);

    Boolean isAdmin(String userId);

    Boolean isAdmin();


}
