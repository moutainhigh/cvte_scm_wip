package com.cvte.scm.demo.boot.config;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.cloud.interceptor.OkHttpTokenInterceptor;
import com.cvte.csb.toolkit.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 12:28
 */
@Component
public class DefaultUserTokenConfig implements OkHttpTokenInterceptor.IUserTokenConfig {

    @Override
    public String getUserToken() {
        String userToken = CurrentContext.getJWTToken();
        // 诸如登录相关的接口在调用BSM时，不会有会话信息，为了避免在OkHttpTokenInterceptor拦截逻辑里设置header抛出异常，指定"none"
        return StringUtils.isBlank(userToken) ? "none" : userToken;
    }
}
