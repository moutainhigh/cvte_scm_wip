package com.cvte.scm.wip.controller.common.user;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 15:06
 */
@Api(tags = "系统用户接口")
@RestController
@RequestMapping("/admin/sys/user")
@Slf4j
public class SysUserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "根据域账号获取用户信息")
    @GetMapping({"/account/{account}"})
    public RestResponse getUserByAccount(@ApiParam("域账号") @PathVariable String account) {
        return ResponseFactory.getOkResponse(userService.getUserByAccount(account));
    }
}