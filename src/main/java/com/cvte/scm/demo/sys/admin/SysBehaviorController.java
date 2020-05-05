package com.cvte.scm.demo.sys.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.demo.common.constants.ResponseDefinition;
import com.cvte.scm.demo.sys.service.SysBehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 15:05
 */
@Api(tags = "系统行为接口")
@RestController
@RequestMapping("/admin/sys/behavior")
@Slf4j
public class SysBehaviorController {

    @Autowired
    private SysBehaviorService sysBehaviorService;

    @ApiOperation(value = "系统登出处理")
    @PostMapping("/logout")
    public RestResponse logout() {
        sysBehaviorService.doLogout();
        return new RestResponse(ResponseDefinition.SUCCESS_CODE, ResponseDefinition.SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "系统登录")
    @PostMapping("/login")
    public RestResponse login(@ApiParam("用户账号") @RequestParam("account") String account,
                              @ApiParam("密码") @RequestParam("password") String password) {
        RestResponse restResponse = sysBehaviorService.doLogin(account, password);
        return restResponse;
    }

    @ApiOperation(value = "获取登录用户简单信息")
    @GetMapping("/me_info")
    public RestResponse me_info(@RequestParam("id") String id) {
        RestResponse restResponse = sysBehaviorService.getCurrentUserInfo(id);
        return restResponse;
    }

}