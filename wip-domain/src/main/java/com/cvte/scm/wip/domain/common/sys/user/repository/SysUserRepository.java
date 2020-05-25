package com.cvte.scm.wip.domain.common.sys.user.repository;

import com.cvte.scm.wip.domain.common.sys.user.entity.SysUserEntity;

/**
 * @author zy
 * @date 2020-05-25 09:48
 **/
public interface SysUserRepository {

    SysUserEntity selectByPrimaryKey(String id);

}

