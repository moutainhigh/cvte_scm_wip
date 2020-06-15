package com.cvte.scm.wip.domain;

import com.cvte.csb.wfp.api.sdk.service.IWorkflowExtendedService;
import com.cvte.scm.wip.common.base.domain.DomainEventPublisher;
import com.cvte.scm.wip.common.base.domain.EventListener;
import com.cvte.scm.wip.common.event.SimpleEventBus;
import com.cvte.scm.wip.domain.common.bu.repository.*;
import com.cvte.scm.wip.domain.common.health.repository.HealthRepository;
import com.cvte.scm.wip.domain.common.serial.SerialNoGenerationService;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.common.user.service.*;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.common.workflow.service.WorkFlowService;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillDetailRepository;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.domain.core.changebill.service.SourceChangeBillService;
import com.cvte.scm.wip.domain.core.item.repository.ScmItemRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.*;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillHeaderRepository;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillLineRepository;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkMoRepository;
import com.cvte.scm.wip.domain.core.rework.service.EbsReworkBillHeaderService;
import com.cvte.scm.wip.domain.core.rework.service.OcsRwkBillService;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleAdaptRepository;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleItemRepository;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleRepository;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleWfRepository;
import com.cvte.scm.wip.domain.core.subrule.service.SubRuleExecuteLogService;
import org.mockito.Mockito;
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
    public SysBuDeptRepository sysBuDeptService() {
        return Mockito.mock(SysBuDeptRepository.class);
    }

    @Bean
    public SysBuRepository sysBuDeptRepository() {
        return Mockito.mock(SysBuRepository.class);
    }

    @Bean
    public HealthRepository HealthRepository() {
        return Mockito.mock(HealthRepository.class);
    }

    @Bean
    public SerialNoGenerationService serialNoGenerationService() {
        return Mockito.mock(SerialNoGenerationService.class);
    }

    @Bean
    public AccessTokenService accessTokenService() {
        return Mockito.mock(AccessTokenService.class);
    }

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return Mockito.mock(StringRedisTemplate.class);
    }

    @Bean
    public BehaviorService behaviorService() {
        return Mockito.mock(BehaviorService.class);
    }

    @Bean
    public MultiOrgSwitchService multiOrgSwitchService() {
        return Mockito.mock(MultiOrgSwitchService.class);
    }

    @Bean
    public OrgService orgService() {
        return Mockito.mock(OrgService.class);
    }

    @Bean
    public PostService postService() {
        return Mockito.mock(PostService.class);
    }

    @Bean
    public RoleService roleService() {
        return Mockito.mock(RoleService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public ViewService ViewService() {
        return Mockito.mock(ViewService.class);
    }

    @Bean
    public IWorkflowExtendedService iWorkflowExtendedService() {
        return Mockito.mock(IWorkflowExtendedService.class);
    }

    @Bean
    public WorkFlowService WorkFlowService() {
        return Mockito.mock(WorkFlowService.class);
    }

    @Bean
    public ChangeBillDetailRepository changeBillDetailRepository() {
        return Mockito.mock(ChangeBillDetailRepository.class);
    }

    @Bean
    public ChangeBillRepository changeBillRepository() {
        return Mockito.mock(ChangeBillRepository.class);
    }

    @Bean
    public SourceChangeBillService sourceChangeBillService() {
        return Mockito.mock(SourceChangeBillService.class);
    }

    @Bean
    public ScmItemRepository scmItemRepository() {
        return Mockito.mock(ScmItemRepository.class);
    }

    @Bean
    public ReqInsDetailRepository reqInsDetailRepository() {
        return Mockito.mock(ReqInsDetailRepository.class);
    }

    @Bean
    public ReqInsRepository reqInsRepository() {
        return Mockito.mock(ReqInsRepository.class);
    }

    @Bean
    public WipEbsReqLogRepository wipEbsReqLogRepository() {
        return Mockito.mock(WipEbsReqLogRepository.class);
    }

    @Bean
    public WipReqHeaderRepository wipReqHeaderRepository() {
        return Mockito.mock(WipReqHeaderRepository.class);
    }

    @Bean
    public WipReqInterfaceRepository wipReqInterfaceRepository() {
        return Mockito.mock(WipReqInterfaceRepository.class);
    }

    @Bean
    public WipReqLineRepository wipReqLineRepository() {
        return Mockito.mock(WipReqLineRepository.class);
    }

    @Bean
    public WipReqLogRepository wipReqLogRepository() {
        return Mockito.mock(WipReqLogRepository.class);
    }

    @Bean
    public WipReqLotIssuedRepository wipReqLotIssuedRepository() {
        return Mockito.mock(WipReqLotIssuedRepository.class);
    }

    @Bean
    public WipReqMtrsRepository wipReqMtrsRepository() {
        return Mockito.mock(WipReqMtrsRepository.class);
    }

    @Bean
    public WipReqPrintLogRepository wipReqPrintLogRepository() {
        return Mockito.mock(WipReqPrintLogRepository.class);
    }

    @Bean
    public XxwipMoInterfaceRepository xxwipMoInterfaceRepository() {
        return Mockito.mock(XxwipMoInterfaceRepository.class);
    }

    @Bean
    public WipReworkBillHeaderRepository wipReworkBillHeaderRepository() {
        return Mockito.mock(WipReworkBillHeaderRepository.class);
    }

    @Bean
    public WipReworkBillLineRepository wipReworkBillLineRepository() {
        return Mockito.mock(WipReworkBillLineRepository.class);
    }

    @Bean
    public WipReworkMoRepository wipReworkMoRepository() {
        return Mockito.mock(WipReworkMoRepository.class);
    }

    @Bean
    public EbsReworkBillHeaderService ebsReworkBillHeaderService() {
        return Mockito.mock(EbsReworkBillHeaderService.class);
    }

    @Bean
    public OcsRwkBillService ocsRwkBillService() {
        return Mockito.mock(OcsRwkBillService.class);
    }

    @Bean
    public WipSubRuleAdaptRepository wipSubRuleAdaptRepository() {
        return Mockito.mock(WipSubRuleAdaptRepository.class);
    }

    @Bean
    public WipSubRuleItemRepository wipSubRuleItemRepository() {
        return Mockito.mock(WipSubRuleItemRepository.class);
    }

    @Bean
    public WipSubRuleRepository wipSubRuleRepository() {
        return Mockito.mock(WipSubRuleRepository.class);
    }

    @Bean
    public WipSubRuleWfRepository wipSubRuleWfRepository() {
        return Mockito.mock(WipSubRuleWfRepository.class);
    }

    @Bean
    public SubRuleExecuteLogService subRuleExecuteLogService() {
        return Mockito.mock(SubRuleExecuteLogService.class);
    }

    @Bean
    public WipLotRepository wipLotRepository() {
        return Mockito.mock(WipLotRepository.class);
    }

    @Bean
    public EventListener eventListener() {
        return Mockito.mock(EventListener.class);
    }

}