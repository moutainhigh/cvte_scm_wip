package com.cvte.scm.wip.infrastructure.boot.interceptor;

import com.cvte.csb.jwt.context.CurrentContext;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.cvte.csb.jwt.constants.CommonConstant.IAC_TOKEN;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 14:24
 */
@Component
@Slf4j
public class ApiVerifyInterceptor implements HandlerInterceptor {

    @Autowired
    private AccessTokenService accessTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //IAC校验
        accessTokenService.iacVerify(request.getHeader(IAC_TOKEN));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) throws Exception {
        //销毁线程上下文信息
        CurrentContext.destroy();
    }
}
