package com.cvte.scm.wip.domain.core.subrule.service;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.AuditorNodeEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.workflow.service.WorkFlowService;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleWfEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleWfRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.AuditorVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/2/26 20:15
 */
@Slf4j
@Component
public class WipSubRuleWfJob {

    private WorkFlowService workFlowService;
    private WipSubRuleWfRepository wipSubRuleWfRepository;

    public WipSubRuleWfJob(WorkFlowService workFlowService, WipSubRuleWfRepository wipSubRuleWfRepository) {
        this.workFlowService = workFlowService;
        this.wipSubRuleWfRepository = wipSubRuleWfRepository;
    }

    @Async("taskExecutor")
    public void asyncUpdate(String formInstance) {
        try {
            // 睡眠,等待ekp提交事务,得到最新审核人
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("更新当前审核人异常, processId = {}, errorMsg = {}", formInstance, e.getMessage());
        }
        List<AuditorVO> auditorList = workFlowService.getAuditor(formInstance);
        if (ListUtil.notEmpty(auditorList)) {
            wipSubRuleWfRepository.deleteByIdAndNode(formInstance, AuditorNodeEnum.CURRENT_AUDITOR.getCode());
        }
        for (AuditorVO auditor : auditorList) {
            WipSubRuleWfEntity subRuleWf = new WipSubRuleWfEntity();
            subRuleWf.setId(UUIDUtils.get32UUID())
                    .setRuleId(formInstance)
                    .setNode(AuditorNodeEnum.CURRENT_AUDITOR.getCode())
                    .setUserId(auditor.getLoginName())
                    .setUserName(auditor.getName());
            EntityUtils.writeStdCrtInfoToEntity(subRuleWf, "WorkFlow");
            wipSubRuleWfRepository.insert(subRuleWf);
        }
    }
}
