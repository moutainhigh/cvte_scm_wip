package com.cvte.scm.wip.controller.common.user;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.cvte.csb.jwt.constants.CommonConstant.IAC_TOKEN;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 15:06
 */
@Api(tags = "系统用户接口")
@RestController
@RequestMapping("/admin/sys/user")
@Slf4j
public class SysUserController {

    private final UserService sysUserService;
    private AccessTokenService accessTokenService;

    public SysUserController(UserService sysUserService, AccessTokenService accessTokenService) {
        this.sysUserService = sysUserService;
        this.accessTokenService = accessTokenService;
    }

    @ApiOperation(value = "根据域账号获取用户信息")
    @GetMapping({"/account/{account}"})
    public RestResponse getUserByAccount(@ApiParam("域账号") @PathVariable String account, HttpServletRequest request) {
        accessTokenService.iacVerify(request.getHeader(IAC_TOKEN));
        return ResponseFactory.getOkResponse(sysUserService.getUserByAccount(account));
    }
}