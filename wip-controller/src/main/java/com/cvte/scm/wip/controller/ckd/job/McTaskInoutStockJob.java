package com.cvte.scm.wip.controller.ckd.job;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.scm.wip.common.constants.CommonUserConstant;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcInoutStockWriteBackService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zy
 * @date 2020-05-12 11:45
 **/
@Slf4j
@Component
@JobHander("mcTaskInoutStockJob")
public class McTaskInoutStockJob extends IJobHandler {

    @Autowired
    private WipMcTaskService wipMcTaskService;

    @Autowired
    private WipMcInoutStockWriteBackService writeBackService;


    @Override
    public ReturnT<String> execute(Map<String, Object> map) {

        CurrentContext.setCurrentOperatingUser(CurrentContextUtils.mockOperatingUser(CommonUserConstant.TIMING_TASK));

        // 回写调拨出库数据
        writeBackService.writeBackInoutStock(() -> wipMcTaskService.listMcTaskDeliveringOutStockView());


        // 回写调拨入库数据
        writeBackService.writeBackInoutStock(() -> wipMcTaskService.listMcTaskDeliveringInStockView());


        return new ReturnT<>("调拨单数据回写已完成");
    }




}
