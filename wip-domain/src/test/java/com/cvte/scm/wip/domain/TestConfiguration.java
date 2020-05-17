package com.cvte.scm.wip.domain;

import com.cvte.scm.wip.domain.common.user.service.*;
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
    public MultiOrgSwitchService multiOrgSwitchApiClient() {
        return Mockito.mock(MultiOrgSwitchService.class);
    }

    @Bean
    public OrgService sysOrgApiClient() {
        return Mockito.mock(OrgService.class);
    }

    @Bean
    public UserService sysUserApiClient() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public RoleService sysRoleApiClient() {
        return Mockito.mock(RoleService.class);
    }

    @Bean
    public PostService sysPostApiClient() {
        return Mockito.mock(PostService.class);
    }

    @Bean
    public AuthService authApiClient() {
        return Mockito.mock(AuthService.class);
    }

}