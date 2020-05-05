package com.cvte.scm.wip.controller.common.user;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.common.constants.ResponseDefinition;
import com.cvte.scm.wip.domain.common.user.service.BehaviorService;
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
    private BehaviorService behaviorService;

    @ApiOperation(value = "系统登出处理")
    @PostMapping("/logout")
    public RestResponse logout() {
        behaviorService.doLogout();
        return new RestResponse(ResponseDefinition.SUCCESS_CODE, ResponseDefinition.SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "系统登录")
    @PostMapping("/login")
    public RestResponse login(@ApiParam("用户账号") @RequestParam("account") String account,
                              @ApiParam("密码") @RequestParam("password") String password) {
        RestResponse restResponse = behaviorService.doLogin(account, password);
        return restResponse;
    }

    @ApiOperation(value = "获取登录用户简单信息")
    @GetMapping("/me_info")
    public RestResponse me_info(@RequestParam("id") String id) {
        RestResponse restResponse = behaviorService.getCurrentUserInfo(id);
        return restResponse;
    }

}