package com.cvte.scm.demo;

import com.cvte.scm.demo.client.datapermission.DataPermissionApiClient;
import com.cvte.scm.demo.client.sys.auth.AuthApiClient;
import com.cvte.scm.demo.client.sys.base.*;
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
    public MultiOrgSwitchApiClient multiOrgSwitchApiClient() {
        return Mockito.mock(MultiOrgSwitchApiClient.class);
    }

    @Bean
    public SysOrgApiClient sysOrgApiClient() {
        return Mockito.mock(SysOrgApiClient.class);
    }

    @Bean
    public SysUserApiClient sysUserApiClient() {
        return Mockito.mock(SysUserApiClient.class);
    }

    @Bean
    public SysRoleApiClient sysRoleApiClient() {
        return Mockito.mock(SysRoleApiClient.class);
    }

    @Bean
    public SysPostApiClient sysPostApiClient() {
        return Mockito.mock(SysPostApiClient.class);
    }

    @Bean
    public AuthApiClient authApiClient() {
        return Mockito.mock(AuthApiClient.class);
    }

    @Bean
    public DataPermissionApiClient dataPermissionApiClient() {
        return Mockito.mock(DataPermissionApiClient.class);
    }
}