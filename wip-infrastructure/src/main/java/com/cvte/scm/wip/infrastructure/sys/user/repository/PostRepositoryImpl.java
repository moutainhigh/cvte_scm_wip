package com.cvte.scm.wip.infrastructure.sys.user.repository;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.UserEntity;
import com.cvte.scm.wip.domain.common.user.repository.PostRepository;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.SysPostApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysPost;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:37
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class PostRepositoryImpl implements PostRepository {

    @Autowired
    private SysPostApiClient sysPostApiClient;

    @Override
    public List<UserEntity> getUserListByPostId(String postId) {
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
    public UserEntity getUserByObjectTypeAndId(String postId, String userId) {
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
