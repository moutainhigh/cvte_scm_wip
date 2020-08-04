package com.cvte.scm.wip.controller.requirement.job;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.requirement.entity.WipEbsReqLogEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqInterfaceEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipEbsReqLogRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqInterfaceRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.EbsReqProcessStatusEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/12 14:42
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
@JobHander(value = "wipEbsReqConsumeAlert")
public class WipEbsReqConsumeAlertJobHandler extends IJobHandler {

    private WipEbsReqLogRepository ebsReqLogRepository;
    private WipReqInterfaceRepository reqInterfaceRepository;

    public WipEbsReqConsumeAlertJobHandler(WipEbsReqLogRepository ebsReqLogRepository, WipReqInterfaceRepository reqInterfaceRepository) {
        this.ebsReqLogRepository = ebsReqLogRepository;
        this.reqInterfaceRepository = reqInterfaceRepository;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) throws Exception {
        Date thirtyMinutesFromNow = Date.from(LocalDateTime.now().minusMinutes((Integer)map.get("cycle")).atZone(ZoneId.systemDefault()).toInstant());
        Date now = new Date();
        List<WipEbsReqLogEntity> ebsReqLogList = ebsReqLogRepository.selectBetweenTimeInStatus(thirtyMinutesFromNow, now, EbsReqProcessStatusEnum.FAILED.getCode());
        List<WipReqInterfaceEntity> reqInterfaceList = reqInterfaceRepository.selectBetweenTimeInStatus(thirtyMinutesFromNow, now, ProcessingStatusEnum.PENDING.getCode(), ProcessingStatusEnum.EXCEPTION.getCode());
        if (ListUtil.empty(ebsReqLogList) && ListUtil.empty(reqInterfaceList)) {
            return new ReturnT<>(null);
        }
        StringBuilder alertMsg = new StringBuilder();
        alertMsg.append("EBS->WIP消费数据出现异常,请排查!\n");
        alertMsg.append("wip_ebs_req_log's log_id: ");
        if (ListUtil.notEmpty(ebsReqLogList)) {
            alertMsg.append("(").append(ebsReqLogList.parallelStream().map(log -> "'" + log.getLogId() + "'").collect(Collectors.joining(","))).append(")");
        }
        alertMsg.append("\n ");
        alertMsg.append("wip_req_interface's interface_id: ");
        if (ListUtil.notEmpty(reqInterfaceList)) {
            alertMsg.append("(").append(reqInterfaceList.parallelStream().map(log -> "'" + log.getInterfaceInId() + "'").collect(Collectors.joining(","))).append(")");
        }
        alertMsg.append("\n ");
        return new ReturnT<>(ReturnT.FAIL_CODE, alertMsg.toString());
    }
}
