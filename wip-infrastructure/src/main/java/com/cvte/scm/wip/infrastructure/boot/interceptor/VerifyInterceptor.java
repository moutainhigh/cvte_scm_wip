package com.cvte.scm.wip.infrastructure.boot.interceptor;

import com.cvte.csb.base.commons.OperatingSystem;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.authorizations.InvalidTokenException;
import com.cvte.csb.jwt.claim.JwtClaim;
import com.cvte.csb.jwt.util.JWTUtil;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.context.GlobalContext;
import com.cvte.scm.wip.domain.common.user.service.BehaviorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 14:29
 */
@Slf4j
@Component
public class VerifyInterceptor implements HandlerInterceptor {

    @Autowired
    private BehaviorService behaviorService;


    @Value("${server.appId}")
    private String appId;
    @Value("${server.systemId}")
    private String systemId;
    @Value("${server.isSingleLoad}")
    private Boolean isSingleLoad;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String userToken = request.getHeader("x-auth-token");
        String userOrgRelationId = request.getHeader("x-org-id");
        // 必须先初始化系统信息上下文，后续初始化用户相关会话时依赖
        initSystemContext(request);
        // 初始化用户相关上下文
        initUserContext(userToken, request.getRemoteAddr());
        // 初始化用户组织上下文
        initUserUnitContext(userOrgRelationId);


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) throws Exception {
        // 销毁线程上下文信息
        CurrentContext.destroy();
        GlobalContext.destroy();
    }


    private void initUserUnitContext(String userOrgRelationId) {
        if (StringUtils.isBlank(userOrgRelationId) || "undefined".equalsIgnoreCase(userOrgRelationId)) {
            return;
        }
        behaviorService.initUserUnitContext(userOrgRelationId);
    }


    private void initUserContext(String userToken, String remoteAddress) {
        if (StringUtils.isBlank(userToken)) {
            throw new InvalidTokenException("token为空");
        }
        // feign调用时，会将userToken写入http header，需要先行注入
        CurrentContext.setJWTToken(userToken);

        JwtClaim claim = JWTUtil.getJwtClaimFromToken(userToken);
        behaviorService.initUserContext(claim.getSub(), remoteAddress);

    }

    private void initSystemContext(HttpServletRequest request) {
        String requestAppId = request.getHeader("appId");
        String requestSystemId = request.getHeader("systemId");
        String requestIsSingleLoad = request.getHeader("isSingleLoad");

        CurrentContext.setCurrentSystem(new OperatingSystem(
                StringUtils.isNotBlank(requestAppId) ? requestAppId : appId,
                StringUtils.isNotBlank(requestSystemId) ? requestSystemId : systemId,
                StringUtils.isNotBlank(requestIsSingleLoad) ? Boolean.valueOf(requestIsSingleLoad) : isSingleLoad)
        );
    }
}
