package com.cvte.scm.demo.sys.service.base.impl;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.demo.client.common.dto.FeignResult;
import com.cvte.scm.demo.client.sys.base.SysPostApiClient;
import com.cvte.scm.demo.client.sys.base.dto.SysPost;
import com.cvte.scm.demo.client.sys.base.dto.SysUser;
import com.cvte.scm.demo.sys.service.base.SysPostService;
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
public class SysPostServiceImpl implements SysPostService {

    @Autowired
    private SysPostApiClient sysPostApiClient;

    @Override
    public List<SysUser> getUserListByPostId(String postId) {
        if(StringUtils.isEmpty(postId)) {
            throw new ParamsRequiredException("postId为空，无法获取岗位下用户列表");
        }
        FeignResult<List<SysUser>> feignResult = sysPostApiClient.getUserListByPostId(postId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取岗位下用户列表失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public SysPost getPost(String id) {
        if(StringUtils.isEmpty(id)) {
            throw new ParamsRequiredException("postId为空，无法获取岗位信息");
        }
        FeignResult<SysPost> feignResult = sysPostApiClient.getPost(id);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取岗位信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public SysUser getUserByPostIdAndUserId(String postId, String userId) {
        if(StringUtils.isEmpty(postId) || StringUtils.isEmpty(userId)) {
            throw new ParamsRequiredException("postId/userId为空，无法获取指定岗位下的指定用户");
        }
        FeignResult<SysUser> feignResult = sysPostApiClient.getUserByObjectTypeAndId(postId, userId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取指定岗位下的指定用户失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }
}
