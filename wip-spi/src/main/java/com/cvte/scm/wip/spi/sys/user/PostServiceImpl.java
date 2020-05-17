package com.cvte.scm.wip.spi.sys.user;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserEntity;
import com.cvte.scm.wip.domain.common.user.service.PostService;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.SysPostApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysPost;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 09:47
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class PostServiceImpl implements PostService {

    private SysPostApiClient sysPostApiClient;

    public PostServiceImpl(SysPostApiClient sysPostApiClient) {
        this.sysPostApiClient = sysPostApiClient;
    }

    @Override
    public List<UserEntity> getUserListByPostId(String postId) {
        if(StringUtils.isEmpty(postId)) {
            throw new ParamsRequiredException("postId为空，无法获取岗位下用户列表");
        }
        FeignResult<List<SysUser>> feignResult = sysPostApiClient.getUserListByPostId(postId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            List<UserEntity> userEntityList = new ArrayList<>();
            for (SysUser sysUser : feignResult.getData()) {
                UserEntity userEntity = new UserEntity();
                BeanUtils.copyProperties(sysUser, userEntity);
                userEntityList.add(userEntity);
            }
            return userEntityList;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取岗位下用户列表失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public PostEntity getPost(String id) {
        if(StringUtils.isEmpty(id)) {
            throw new ParamsRequiredException("postId为空，无法获取岗位信息");
        }
        FeignResult<SysPost> feignResult = sysPostApiClient.getPost(id);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            PostEntity postEntity = new PostEntity();
            BeanUtils.copyProperties(feignResult.getData(), postEntity);
            return postEntity;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取岗位信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public UserEntity getUserByPostIdAndUserId(String postId, String userId) {
        if(StringUtils.isEmpty(postId) || StringUtils.isEmpty(userId)) {
            throw new ParamsRequiredException("postId/userId为空，无法获取指定岗位下的指定用户");
        }
        FeignResult<SysUser> feignResult = sysPostApiClient.getUserByObjectTypeAndId(postId, userId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(feignResult.getData(), userEntity);
            return userEntity;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取指定岗位下的指定用户失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

}
