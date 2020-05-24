package com.cvte.scm.wip.controller.rework.job;

import com.cvte.scm.wip.domain.core.rework.service.WipReworkBillSyncService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/14 18:56
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
@JobHander(value = "syncRwkBillHFromEbsJob")
public class SyncRwkBillHFromEbsJobHandler extends IJobHandler {

    private WipReworkBillSyncService wipReworkBillSyncService;

    public SyncRwkBillHFromEbsJobHandler(WipReworkBillSyncService wipReworkBillSyncService) {
        this.wipReworkBillSyncService = wipReworkBillSyncService;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) throws Exception {
        Integer timeRange = (Integer)map.get("timeRange");
        if (Objects.isNull(timeRange)) {
            timeRange = 30;
        }
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(timeRange);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        String message = wipReworkBillSyncService.syncBillFromEbs(date, null);
        ReturnT<String> returnT = ReturnT.SUCCESS;
        returnT.setMsg(message);
        return returnT;
    }
}
