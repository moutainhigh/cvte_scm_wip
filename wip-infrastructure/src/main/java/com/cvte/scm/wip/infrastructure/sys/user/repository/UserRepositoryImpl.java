package com.cvte.scm.wip.infrastructure.sys.user.repository;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.scm.wip.domain.common.user.entity.GroupEntity;
import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.repository.UserRepository;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.SysUserApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysGroup;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysPost;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.UserBaseDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:17
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private SysUserApiClient sysUserApiClient;

    @Override
    public UserBaseEntity getSysUserDetail(String id) {
        FeignResult<UserBaseDTO> feignResult = sysUserApiClient.getSysUserDetail(id);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            UserBaseEntity userBaseEntity = new UserBaseEntity();
            BeanUtils.copyProperties(feignResult.getData(), userBaseEntity);
            return userBaseEntity;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public UserBaseEntity getSysUserDetailByAccount(String account) {
        FeignResult<UserBaseDTO> feignResult = sysUserApiClient.getSysUserDetailByAccount(account);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            UserBaseEntity userBaseEntity = new UserBaseEntity();
            BeanUtils.copyProperties(feignResult.getData(), userBaseEntity);
            return userBaseEntity;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public List<PostEntity> listUserPosts(String userId) {
        FeignResult<List<SysPost>> feignResult = sysUserApiClient.listUserPosts(userId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            List<PostEntity> postEntityList = new ArrayList<>();
            for (SysPost sysPost : feignResult.getData()) {
                PostEntity postEntity = new PostEntity();
                BeanUtils.copyProperties(sysPost, postEntity);
                postEntityList.add(postEntity);
            }
            return postEntityList;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户岗位信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public List<GroupEntity> listUserGroups(String userId) {
        FeignResult<List<SysGroup>> feignResult = sysUserApiClient.listUserGroups(userId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            List<GroupEntity> groupEntityList = new ArrayList<>();
            for (SysGroup sysGroup : feignResult.getData()) {
                GroupEntity groupEntity = new GroupEntity();
                BeanUtils.copyProperties(sysGroup, groupEntity);
                groupEntityList.add(groupEntity);
            }
            return groupEntityList;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户群组信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }
}
