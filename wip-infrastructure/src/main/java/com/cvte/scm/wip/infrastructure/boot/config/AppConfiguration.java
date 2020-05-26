package com.cvte.scm.wip.infrastructure.boot.config;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.cloud.interceptor.SleuthLogInterceptor;
import com.cvte.csb.web.log.interceptor.MvcLogInterceptor;
import com.cvte.csb.web.log.state.ICurrentCaller;
import com.cvte.csb.web.log.state.ICurrentInfo;
import com.cvte.scm.wip.infrastructure.boot.interceptor.ApiVerifyInterceptor;
import com.cvte.scm.wip.infrastructure.boot.interceptor.VerifyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 拦截器配置类
 * @Author: wufeng
 * @Date: 2019/12/23 15:26
 */
@Configuration
public class AppConfiguration extends WebMvcConfigurerAdapter {

    // 此拦截器中有Autowired注入，需要使用bean注解提前加载拦截器
    @Bean
    public HandlerInterceptor verifyInterceptor() {
        return new VerifyInterceptor();
    }

    @Bean
    public HandlerInterceptor apiVerifyInterceptor() {
        return new ApiVerifyInterceptor();
    }

    @Bean
    public SleuthLogInterceptor sleuthLogInterceptor() {
        return new SleuthLogInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(sleuthLogInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/portal/v1/**"
                        , "/api/health/**");

        //管理后台接口 /admin/开头
        registry.addInterceptor(verifyInterceptor()).addPathPatterns("/admin/**")
                .excludePathPatterns("/**/*export*"
                        , "kl/admin/sys/behavior/login"
                        , "/admin/sys/behavior/me_info"
                        , "/admin/sys/user/account/*");

        //服务接口 /api/开头
        registry.addInterceptor(apiVerifyInterceptor()).addPathPatterns("/api/**")
                .excludePathPatterns("/api/health/**");

        ICurrentInfo currentInfo = new ICurrentInfo() {
            @Override
            public String getUserAccount() {
                if (CurrentContext.getCurrentOperatingUser() == null) {
                    return "";
                } else {
                    return CurrentContext.getCurrentOperatingUser().getAccount();
                }
            }
        };

        ICurrentCaller callerInfo = new ICurrentCaller() {
            @Override
            public String getCaller() {
                if (CurrentContext.getCurrentOperatingSystem() == null) {
                    return "";
                } else {
                    return CurrentContext.getCurrentOperatingSystem().getSystemId();
                }
            }
        };

        // 日志处理拦截器
        registry.addInterceptor(new MvcLogInterceptor(currentInfo, callerInfo)).addPathPatterns("/**").excludePathPatterns("/api/health/**");

        super.addInterceptors(registry);
    }



}