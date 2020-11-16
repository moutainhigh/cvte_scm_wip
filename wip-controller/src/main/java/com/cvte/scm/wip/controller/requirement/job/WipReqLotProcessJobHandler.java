package com.cvte.scm.wip.controller.requirement.job;

import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotProcessService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/11/7 16:17
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
@JobHander(value = "wipReqLotProcess")
public class WipReqLotProcessJobHandler extends IJobHandler {

    private WipReqLotProcessService wipReqLotProcessService;

    public WipReqLotProcessJobHandler(WipReqLotProcessService wipReqLotProcessService) {
        this.wipReqLotProcessService = wipReqLotProcessService;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) {
        return new ReturnT<>(wipReqLotProcessService.getAndProcess());
    }

}
