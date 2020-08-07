package com.cvte.scm.wip.controller.subrule.job;

import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleRepository;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : jf
 * Date    : 2020.04.20
 * Time    : 11:09
 * Email   ：jiangfeng7128@cvte.com
 */
@Service
@JobHander(value = "refreshSubRuleExpiredStatus")
public class RefreshSubRuleExpiredStatusJobHandle extends IJobHandler {

    private WipSubRuleRepository ruleRepository;

    public RefreshSubRuleExpiredStatusJobHandle(WipSubRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) {
        XxlJobLogger.log("开始刷新已失效规则的状态！");
        /* 过了失效时间的临时代用单规则，状态自动变成"已失效" */
        int rowsAffected = ruleRepository.expire();
        XxlJobLogger.log(String.format("成功设置了 %d 条临时代用单的状态为已失效！", rowsAffected));
        return new ReturnT<>(null);
    }
}