package com.cvte.scm.wip.domain.common.user.repository;

import com.cvte.scm.wip.domain.common.user.entity.GroupEntity;
import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:13
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public interface UserRepository {

    UserBaseEntity getSysUserDetail(String id);

    UserBaseEntity getSysUserDetailByAccount(String account);

    List<PostEntity> listUserPosts(String userId);

    List<GroupEntity> listUserGroups(String userId);

}
