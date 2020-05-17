package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserEntity;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/11/9 13:44
 */
public interface PostService {

    List<UserEntity> getUserListByPostId(String postId);

    PostEntity getPost(String id);

    UserEntity getUserByPostIdAndUserId(String postId, String userId);

}
