package com.cvte.scm.wip.controller.requirement.job;

import com.cvte.scm.wip.domain.core.requirement.service.WipReqInterfaceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : jf
 * Date    : 2020.02.28
 * Time    : 14:26
 * Email   ：jiangfeng7128@cvte.com
 */
@Slf4j
@Service
@JobHander(value = "wipInterfaceOmissionExecution")
public class WipInterfaceJobHandle extends IJobHandler {

    private final WipReqInterfaceService wipReqInterfaceService;

    public WipInterfaceJobHandle(WipReqInterfaceService wipReqInterfaceService) {
        this.wipReqInterfaceService = wipReqInterfaceService;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) {
        XxlJobLogger.log("开始执行接口遗漏数据执行的定时任务");
        wipReqInterfaceService.executeOmissionData();
        XxlJobLogger.log("接口遗漏数据执行的定时任务执行结束");
        return ReturnT.SUCCESS;
    }
}