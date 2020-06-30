package com.cvte.scm.wip.domain;

import com.cvte.scm.wip.domain.common.attachment.repository.WipAttachmentRepository;
import com.cvte.scm.wip.domain.common.log.repository.WipOperationLogRepository;
import com.cvte.scm.wip.domain.common.sys.user.repository.SysUserRepository;
import com.cvte.scm.wip.domain.core.ckd.repository.*;
import com.cvte.scm.wip.domain.core.scm.repository.ScmViewCommonRepository;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.service.EbsInvokeService;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/21 16:36
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Configuration
public class TestCkdConfiguration {

    @Bean
    public WipAttachmentRepository wipAttachmentRepository() {
        return Mockito.mock(WipAttachmentRepository.class);
    }

    @Bean
    public WipOperationLogRepository wipOperationLogRepository() {
        return Mockito.mock(WipOperationLogRepository.class);
    }

    @Bean
    public WipMcInoutStockLineRepository wipMcInoutStockLineRepository() {
        return Mockito.mock(WipMcInoutStockLineRepository.class);
    }

    @Bean
    public WipMcInoutStockRepository WipMcInoutStockRepository() {
        return Mockito.mock(WipMcInoutStockRepository.class);
    }

    @Bean
    public WipMcReqRepository wipMcReqRepository() {
        return Mockito.mock(WipMcReqRepository.class);
    }

    @Bean
    public WipMcReqToTaskRepository wipMcReqToTaskRepository() {
        return Mockito.mock(WipMcReqToTaskRepository.class);
    }

    @Bean
    public WipMcTaskLineRepository wipMcTaskLineRepository() {
        return Mockito.mock(WipMcTaskLineRepository.class);
    }

    @Bean
    public WipMcTaskLineVersionRepository wipMcTaskLineVersionRepository() {
        return Mockito.mock(WipMcTaskLineVersionRepository.class);
    }

    @Bean
    public WipMcTaskRepository wipMcTaskRepository() {
        return Mockito.mock(WipMcTaskRepository.class);
    }

    @Bean
    public WipMcTaskVersionRepository WipMcTaskVersionRepository() {
        return Mockito.mock(WipMcTaskVersionRepository.class);
    }

    @Bean
    public WipMcWfRepository WipMcWfRepository() {
        return Mockito.mock(WipMcWfRepository.class);
    }

    @Bean
    public EbsInvokeService ebsInvokeService() {
        return Mockito.mock(EbsInvokeService.class);
    }

    @Bean
    public ModelMapper modelMapper() {
        return Mockito.mock(ModelMapper.class);
    }

    @Bean
    public SysUserRepository sysUserRepository() {
        return Mockito.mock(SysUserRepository.class);
    }

    @Bean
    public ScmViewCommonRepository scmViewCommonRepository() {
        return Mockito.mock(ScmViewCommonRepository.class);
    }

}
