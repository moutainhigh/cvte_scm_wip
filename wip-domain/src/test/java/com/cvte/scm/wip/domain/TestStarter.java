package com.cvte.scm.wip.domain;

import com.ctrip.framework.apollo.spring.annotation.EnableCSBConfig;
import com.cvte.csb.core.annotation.EnableCSB;
import com.cvte.csb.jdbc.mybatis.annotation.EnableCSBMybatis;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:21
 */
@EnableCSB
@EnableCSBMybatis
@EnableCSBConfig
@ComponentScan({"com.cvte.scm.demo"})
public class TestStarter {

    public static void main(String[] args) {
        SpringApplication.run(TestStarter.class, args);
    }
}