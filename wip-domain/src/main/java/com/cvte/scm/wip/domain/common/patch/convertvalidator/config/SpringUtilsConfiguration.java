package com.cvte.scm.wip.domain.common.patch.convertvalidator.config;

import com.cvte.scm.wip.domain.common.patch.convertvalidator.util.SpringContextUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/27 10:56 上午
 */
@Configuration
public class SpringUtilsConfiguration {
    @ConditionalOnMissingBean(SpringContextUtils.class)
    @Bean
    public SpringContextUtils init(){
        SpringContextUtils springContextUtils = new SpringContextUtils();
        return springContextUtils;
    }
}
