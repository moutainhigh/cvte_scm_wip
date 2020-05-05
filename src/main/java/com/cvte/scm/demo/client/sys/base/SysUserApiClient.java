package com.cvte.scm.demo.client.sys.base;

import com.cvte.scm.demo.client.common.dto.FeignResult;
import com.cvte.scm.demo.client.sys.base.dto.SysGroup;
import com.cvte.scm.demo.client.sys.base.dto.SysPost;
import com.cvte.scm.demo.client.sys.base.dto.UserBaseDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 10:03
 */
@FeignClient("scm-bsm")
public interface SysUserApiClient {

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return
     */
    @GetMapping({"/api/v1/user/{id}"})
    FeignResult<UserBaseDTO> getSysUserDetail(@PathVariable("id") String id);


    /**
     * 通过用户账号获取用户详情
     *
     * @param account 用户账号
     * @return
     */
    @GetMapping({"/api/v1/user/account/{account}"})
    FeignResult<UserBaseDTO> getSysUserDetailByAccount(@PathVariable("account") String account);


    /**
     * 获取用户所属岗位
     *
     * @param userId
     * @return
     */
    @GetMapping("/admin/v1/user/{userId}/post")
    FeignResult<List<SysPost>> listUserPosts(@PathVariable("userId") String userId);


    /**
     * 获取用户所属群组
     *
     * @param userId
     * @return
     */
    @GetMapping("/admin/v1/user/{userId}/group")
    FeignResult<List<SysGroup>> listUserGroups(@PathVariable("userId") String userId);
}
