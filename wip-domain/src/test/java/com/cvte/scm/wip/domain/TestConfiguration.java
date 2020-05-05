package com.cvte.scm.wip.domain;

import com.cvte.scm.wip.domain.common.user.repository.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:22
 */
@Configuration
public class TestConfiguration {

    @Bean
    public MultiOrgSwitchRepository multiOrgSwitchApiClient() {
        return Mockito.mock(MultiOrgSwitchRepository.class);
    }

    @Bean
    public OrgRepository sysOrgApiClient() {
        return Mockito.mock(OrgRepository.class);
    }

    @Bean
    public UserRepository sysUserApiClient() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public RoleRepository sysRoleApiClient() {
        return Mockito.mock(RoleRepository.class);
    }

    @Bean
    public PostRepository sysPostApiClient() {
        return Mockito.mock(PostRepository.class);
    }

    @Bean
    public AuthRepository authApiClient() {
        return Mockito.mock(AuthRepository.class);
    }

}