package com.cvte.scm.wip.domain.core.requirement.service;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqPrintLogEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqPrintLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 投料单打印日志服务类
 *
 * @author jf
 * @since 2020-03-06
 */
@Service
@Transactional
public class WipReqPrintLogService {

    private WipReqPrintLogRepository wipReqPrintLogRepository;

    public WipReqPrintLogService(WipReqPrintLogRepository wipReqPrintLogRepository) {
        this.wipReqPrintLogRepository = wipReqPrintLogRepository;
    }

    public String add(WipReqLineEntity wipReqLine) {
        if (wipReqLine == null) {
            return "记录失败，打印信息为空。";
        } else if (StringUtils.isEmpty(wipReqLine.getHeaderId())) {
            return "记录失败，投料单头ID为空。";
        }
        WipReqPrintLogEntity printLog = new WipReqPrintLogEntity().setPrintLogId(UUIDUtils.getUUID())
                .setHeaderId(wipReqLine.getHeaderId()).setSelectCondition(JSON.toJSONString(wipReqLine))
                .setCrtUser(CurrentContextUtils.getOrDefaultUserId("SCM-WIP")).setCrtTime(new Date());
        wipReqPrintLogRepository.insert(printLog);
        return "";
    }
}