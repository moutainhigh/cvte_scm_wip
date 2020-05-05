package com.cvte.scm.demo;

import com.ctrip.framework.apollo.spring.annotation.EnableCSBConfig;
import com.cvte.csb.cache.annotation.EnableCSBRedisCache;
import com.cvte.csb.cloud.annotation.EnableCSBCloud;
import com.cvte.csb.core.annotation.EnableCSB;
import com.cvte.csb.jdbc.mybatis.annotation.EnableCSBMybatis;
import com.cvte.csb.job.core.EnableCSBJob;
import com.cvte.csb.jwt.annotation.EnableCSBJwt;
import com.cvte.csb.log.annotation.EnableCSBLog;
import com.cvte.csb.msg.annotation.EnableCSBMsg;
import com.cvte.csb.store.annotation.EnableCSBStore;
import com.cvte.csb.web.annotation.EnableCSBWeb;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 12:12
 */
@EnableCSB
@EnableCSBMybatis
@EnableCSBWeb
@EnableCSBConfig
@EnableCSBRedisCache
@EnableCSBJob

@EnableCSBMsg
@EnableCSBLog
@ComponentScan({"com.cvte.scm.demo", "com.cvte.csb.wfp.api.sdk"})
@EnableCSBJwt
@EnableAsync
@EnableCSBCloud
@EnableFeignClients(basePackages = {"com.cvte.scm.demo.client"})
@EnableCSBStore
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Bootstrap.class);
        app.run(args);
    }

}
