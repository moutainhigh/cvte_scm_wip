package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLogEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLogRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 投料单行数据日志实现类
 *
 * @author jf
 * @since 2020-01-01
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
@Slf4j
public class WipReqLogService {

    private WipReqLogRepository wipReqLogRepository;

    public WipReqLogService(WipReqLogRepository wipReqLogRepository) {
        this.wipReqLogRepository = wipReqLogRepository;
    }

    public void addWipReqLog(WipReqLineEntity beforeLine, WipReqLineEntity afterLine, ChangedTypeEnum type) {
        WipReqLogEntity wipReqLog = new WipReqLogEntity().setLogId(UUIDUtils.getUUID()).setLineId(Optional.ofNullable(beforeLine.getLineId()).orElse("null"))
                .setBeforeItemNo(beforeLine.getItemNo()).setAfterItemNo(afterLine.getItemNo())
                .setBeforeItemQty(beforeLine.getReqQty()).setAfterItemQty(afterLine.getReqQty())
                .setBeforeWkpNo(beforeLine.getWkpNo()).setAfterWkpNo(afterLine.getWkpNo())
                .setBeforePosNo(beforeLine.getPosNo()).setAfterPosNo(afterLine.getPosNo())
                .setOperationType(type.getCode()).setHeaderId(beforeLine.getHeaderId());
        EntityUtils.writeStdCrtInfoToEntity(wipReqLog, afterLine.getUpdUser());
        wipReqLogRepository.insert(wipReqLog);
        log.info("日志表写入成功：log_id = " + wipReqLog.getLogId());
    }
    
}