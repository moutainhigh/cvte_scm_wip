package com.cvte.scm.wip.infrastructure.ckd.config;

import com.cvte.scm.wip.domain.core.ckd.processor.McTaskStatusProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zy
 * @date 2020-05-22 09:52
 **/
@Configuration
public class WipCkdConfig {


    @Bean
    public McTaskStatusProcessor initMcTaskStatusProcessor() {
        return new McTaskStatusProcessor();
    }
}
