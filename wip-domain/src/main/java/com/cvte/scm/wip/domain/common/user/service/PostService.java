package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserEntity;
import com.cvte.scm.wip.domain.common.user.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/11/9 13:44
 */
@Slf4j
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<UserEntity> getUserListByPostId(String postId) {
        if(StringUtils.isEmpty(postId)) {
            throw new ParamsRequiredException("postId为空，无法获取岗位下用户列表");
        }
        return postRepository.getUserListByPostId(postId);
    }

    public PostEntity getPost(String id) {
        if(StringUtils.isEmpty(id)) {
            throw new ParamsRequiredException("postId为空，无法获取岗位信息");
        }
        return postRepository.getPost(id);
    }

    public UserEntity getUserByPostIdAndUserId(String postId, String userId) {
        if(StringUtils.isEmpty(postId) || StringUtils.isEmpty(userId)) {
            throw new ParamsRequiredException("postId/userId为空，无法获取指定岗位下的指定用户");
        }
        return postRepository.getUserByObjectTypeAndId(postId, userId);
    }
}
