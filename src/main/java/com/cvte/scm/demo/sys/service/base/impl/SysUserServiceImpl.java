package com.cvte.scm.demo.sys.service.base.impl;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.demo.client.common.dto.FeignResult;
import com.cvte.scm.demo.client.sys.base.SysUserApiClient;
import com.cvte.scm.demo.client.sys.base.dto.SysGroup;
import com.cvte.scm.demo.client.sys.base.dto.SysPost;
import com.cvte.scm.demo.client.sys.base.dto.UserBaseDTO;
import com.cvte.scm.demo.sys.service.base.SysUserService;
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
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserApiClient sysUserApiClient;

    @Override
    public UserBaseDTO getUserByAccount(String account) {
        if(StringUtils.isEmpty(account)) {
            throw new ParamsRequiredException("account为空，无法获取系统用户");
        }
        FeignResult<UserBaseDTO> feignResult = sysUserApiClient.getSysUserDetailByAccount(account);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public UserBaseDTO getEnableUserInfo(String id) {
        if(StringUtils.isEmpty(id)) {
            throw new ParamsRequiredException("id为空，无法获取系统用户");
        }
        FeignResult<UserBaseDTO> feignResult = sysUserApiClient.getSysUserDetail(id);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public List<SysPost> listUserPosts(String userId) {
        if(StringUtils.isEmpty(userId)) {
            throw new ParamsRequiredException("userId为空，无法获取系统用户岗位");
        }
        FeignResult<List<SysPost>> feignResult = sysUserApiClient.listUserPosts(userId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户岗位信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public List<SysGroup> listUserGroups(String userId) {
        if(StringUtils.isEmpty(userId)) {
            throw new ParamsRequiredException("userId为空，无法获取系统用户群组");
        }
        FeignResult<List<SysGroup>> feignResult = sysUserApiClient.listUserGroups(userId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户群组信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }
}
