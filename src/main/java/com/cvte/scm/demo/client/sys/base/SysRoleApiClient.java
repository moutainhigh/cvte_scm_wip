package com.cvte.scm.demo.client.sys.base;

import com.cvte.scm.demo.client.common.dto.FeignResult;
import com.cvte.scm.demo.client.sys.base.dto.SysRoleDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 09:35
 */
@FeignClient("scm-bsm")
@RequestMapping("/admin/v1/role")
public interface SysRoleApiClient {

    /**
     * 根据用户Id获取角色列表，默认当前用户
     *
     * @param userId 用户Id
     * @return
     */
    @GetMapping("/auth_user")
    FeignResult<List<SysRoleDTO>> listRoleByUserId(@RequestParam(value = "userId", required = false) String userId);
}
