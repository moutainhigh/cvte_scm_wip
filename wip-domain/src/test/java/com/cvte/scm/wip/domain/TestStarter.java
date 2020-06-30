package com.cvte.scm.wip.domain;

import com.cvte.scm.wip.common.base.ApplicationContextHelper;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:21
 */
@Import({
        TestConfiguration.class,
        TestCkdConfiguration.class,
})
@ComponentScan({"com.cvte.scm.wip.domain", "com.cvte.scm.wip.common"})
public class TestStarter {

    public static void main(String[] args) {
        SpringApplication.run(TestStarter.class, args);
    }

}