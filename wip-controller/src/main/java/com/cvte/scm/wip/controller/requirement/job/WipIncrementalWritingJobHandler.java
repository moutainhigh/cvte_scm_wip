package com.cvte.scm.wip.controller.requirement.job;

import com.cvte.scm.wip.app.req.line.ReqLineSyncApplication;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : jf
 * Date    : 2020.02.28
 * Time    : 14:26
 * Email   ：jiangfeng7128@cvte.com
 */
@Service
@JobHander(value = "wipIncrementalWriting")
public class WipIncrementalWritingJobHandler extends IJobHandler {

    private ReqLineSyncApplication reqLineSyncApplication;

    public WipIncrementalWritingJobHandler(ReqLineSyncApplication reqLineSyncApplication) {
        this.reqLineSyncApplication = reqLineSyncApplication;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) {
        ReturnT<String> returnT = new ReturnT<>(null);
        try {
            reqLineSyncApplication.doAction(map);
        } catch (RuntimeException re) {
            returnT.setCode(ReturnT.FAIL_CODE);
            returnT.setMsg(re.getMessage());
        }
        return returnT;
    }
}