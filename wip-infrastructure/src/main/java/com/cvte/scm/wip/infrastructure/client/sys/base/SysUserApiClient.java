package com.cvte.scm.wip.infrastructure.client.sys.base;

import com.cvte.csb.sys.base.dto.response.UserDTO;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysGroup;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysPost;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.UserBaseDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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


    /**
     * 获取满足参数条件的所有用户
     *
     * @param name 用户名
     * @return
     */
    @GetMapping({"/admin/v1/user/role_user_unit"})
    FeignResult<Map<String, Object>> listUserByName(@RequestParam(value = "name", required = false) String name);

    /**
     * 获取用户基础和扩展信息
     *
     * @param id 用户ID
     * @return
     */
    @GetMapping("/admin/v1/user/{id}")
    FeignResult<UserDTO> getSysUserFull(@PathVariable("id") String id);

}
