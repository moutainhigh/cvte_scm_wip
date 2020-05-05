package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.user.entity.GroupEntity;
import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 10:07
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserBaseEntity getEnableUserInfo(String id) {
        if(StringUtils.isEmpty(id)) {
            throw new ParamsRequiredException("id为空，无法获取系统用户");
        }
        return userRepository.getSysUserDetail(id);
    }

    public UserBaseEntity getUserByAccount(String account) {
        if(StringUtils.isEmpty(account)) {
            throw new ParamsRequiredException("account为空，无法获取系统用户");
        }
        return userRepository.getSysUserDetailByAccount(account);
    }

    public List<PostEntity> listUserPosts(String userId) {
        if(StringUtils.isEmpty(userId)) {
            throw new ParamsRequiredException("userId为空，无法获取系统用户岗位");
        }
        return userRepository.listUserPosts(userId);
    }

    public List<GroupEntity> listUserGroups(String userId) {
        if(StringUtils.isEmpty(userId)) {
            throw new ParamsRequiredException("userId为空，无法获取系统用户群组");
        }
        return userRepository.listUserGroups(userId);
    }
}
