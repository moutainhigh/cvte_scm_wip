package com.cvte.scm.wip.controller.requirement.job;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : jf
 * Date    : 2020.02.28
 * Time    : 14:26
 * Email   ：jiangfeng7128@cvte.com
 */
@Service
@JobHander(value = "wipIncrementalWriting")
public class WipIncrementalWritingJobHandler extends IJobHandler {

    private WipReqLineRepository linesRepository;

    private WipReqHeaderRepository headerRepository;

    public WipIncrementalWritingJobHandler(WipReqHeaderRepository headerRepository, WipReqLineRepository linesRepository) {
        this.linesRepository = linesRepository;
        this.headerRepository = headerRepository;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) {
        String organizationIdStr = (String)map.get("organizationId");
        if (StringUtils.isBlank(organizationIdStr)) {
            ReturnT<String> returnT = ReturnT.FAIL;
            returnT.setMsg("组织ID不可为空");
            return returnT;
        }
        List<Integer> organizationIdList = Arrays.stream(organizationIdStr.split(",")).map(Integer::parseInt).collect(Collectors.toList());

        // 投料单头增量导入
        List<WipReqHeaderEntity> addedHeaderData = headerRepository.selectAddedData(organizationIdList);
        if (addedHeaderData.isEmpty()) {
            return ReturnT.SUCCESS;
        }
        headerRepository.batchInsert(addedHeaderData);
        XxlJobLogger.log("投料单头增量写入成功！");
        List<String> sourceIdList = addedHeaderData.stream().map(WipReqHeaderEntity::getSourceId).collect(Collectors.toList());
        linesRepository.writeIncrementalData(sourceIdList, organizationIdList);
        XxlJobLogger.log("投料单行增量写入成功！");
        return ReturnT.SUCCESS;
    }
}