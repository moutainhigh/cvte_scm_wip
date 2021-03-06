package com.cvte.scm.wip.infrastructure;

import com.cvte.scm.wip.domain.common.serial.SerialNoGenerationService;
import com.cvte.scm.wip.domain.common.user.service.*;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.core.changebill.service.ChangeBillWriteBackService;
import com.cvte.scm.wip.domain.core.changebill.service.SourceChangeBillService;
import com.cvte.scm.wip.domain.core.rework.service.EbsReworkBillHeaderService;
import com.cvte.scm.wip.domain.core.rework.service.OcsRwkBillService;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.service.EbsInvokeService;
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

    @Bean
    public EbsInvokeService ebsInvokeService() {
        return Mockito.mock(EbsInvokeService.class);
    }

    @Bean
    public ViewService viewService() {
        return Mockito.mock(ViewService.class);
    }

    @Bean
    public ChangeBillWriteBackService changeBillWriteBackService() {
        return Mockito.mock(ChangeBillWriteBackService.class);
    }

    @Bean
    public SourceChangeBillService sourceChangeBillService() {
        return Mockito.mock(SourceChangeBillService.class);
    }

    @Bean
    public SerialNoGenerationService serialNoGenerationService() {
        return Mockito.mock(SerialNoGenerationService.class);
    }


    @Bean
    public OcsRwkBillService ocsRwkBillService() {
        return Mockito.mock(OcsRwkBillService.class);
    }

    @Bean
    public EbsReworkBillHeaderService ebsReworkBillHeaderService() {
        return Mockito.mock(EbsReworkBillHeaderService.class);
    }
}