package com.cvte.scm.wip.domain;

import com.cvte.csb.wfp.api.sdk.service.IWorkflowExtendedService;
import com.cvte.scm.wip.common.base.domain.EventListener;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:22
 */
@Configuration
public class TestConfiguration {

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return Mockito.mock(StringRedisTemplate.class);
    }

    @Bean
    public IWorkflowExtendedService iWorkflowExtendedService() {
        return Mockito.mock(IWorkflowExtendedService.class);
    }

    @Bean
    public EventListener eventListener() {
        return Mockito.mock(EventListener.class);
    }

    @Bean
    public ModelMapper modelMapper() {
        return Mockito.mock(ModelMapper.class);
    }

}