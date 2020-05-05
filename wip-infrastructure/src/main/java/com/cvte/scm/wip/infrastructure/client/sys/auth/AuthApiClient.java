package com.cvte.scm.wip.infrastructure.client.sys.auth;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * BSM中与验证相关的服务
 * @Author: wufeng
 * @Date: 2019/10/18 10:51
 */
@FeignClient("scm-bsm")
@RequestMapping("/admin/v1/auth")
public interface AuthApiClient {

    /**
     * 用户登录
     * @param account 用户账号
     * @param password 密码
     * @return
     */
    @PostMapping("/login")
    RestResponse login(@RequestParam("account") String account,
                       @RequestParam("password") String password);

    /**
     * 获取登录用户信息
     * @return
     */
    @GetMapping("/me")
    RestResponse me();

    /**
     * 获取登录用户简单信息
     * @param id
     * @return
     */
    @GetMapping("/me_info")
    RestResponse getCurrentUserInfo(@RequestParam("id") String id);
}