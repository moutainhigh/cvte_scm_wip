package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.scm.wip.domain.common.user.entity.GroupEntity;
import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 10:07
 */
public interface UserService {

    UserBaseEntity getEnableUserInfo(String id);

    UserBaseEntity getUserByAccount(String account);

    List<PostEntity> listUserPosts(String userId);

    List<GroupEntity> listUserGroups(String userId);

    String getDefaultDimensionByUserId(String userId);

}
