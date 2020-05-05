package com.cvte.scm.wip.infrastructure.client.sys.base;

import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysPost;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


/**
 * @Author: wufeng
 * @Date: 2019/11/9 12:29
 */
@FeignClient("scm-bsm")
public interface SysPostApiClient {

    /**
     * 获取岗位下的所有用户信息
     * @param postId 岗位id
     * @return
     */
    @GetMapping("/api/post/ext/{id}/users")
    FeignResult<List<SysUser>> getUserListByPostId(@PathVariable("id") String postId);

    /**
     * 获取岗位信息
     * @param id
     * @return
     */
    @GetMapping("/admin/v1/post/{id}/information")
    FeignResult<SysPost> getPost(@PathVariable("id") String id);


    /**
     * 根据岗位id获取岗位下的指定用户
     * @param objectId
     * @param userId
     * @return
     */
    @GetMapping({"/admin/v1/post/{id}/user/{userId}"})
    FeignResult<SysUser> getUserByObjectTypeAndId(@PathVariable("id") String objectId, @PathVariable("userId") String userId);
}
