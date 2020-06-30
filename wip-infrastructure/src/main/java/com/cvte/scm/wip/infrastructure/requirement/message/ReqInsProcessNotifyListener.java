package com.cvte.scm.wip.infrastructure.requirement.message;

import com.cvte.csb.core.exception.client.forbiddens.NoOperationRightException;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.EventListener;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.domain.core.changebill.service.ChangeBillWriteBackService;
import com.cvte.scm.wip.domain.core.changebill.valueobject.enums.ChangeBillOptTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.event.ReqInsProcessNotifyEvent;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import com.cvte.scm.wip.infrastructure.changebill.mapper.WipCnBillErrorLogMapper;
import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillErrorLogDO;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/11 11:47
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ReqInsProcessNotifyListener implements EventListener<ReqInsProcessNotifyEvent> {

    private ChangeBillRepository changeBillRepository;
    private ChangeBillWriteBackService changeBillWriteBackService;
    private WipCnBillErrorLogMapper wipCnBillErrorLogMapper;

    public ReqInsProcessNotifyListener(ChangeBillRepository changeBillRepository, ChangeBillWriteBackService changeBillWriteBackService, WipCnBillErrorLogMapper wipCnBillErrorLogMapper) {
        this.changeBillRepository = changeBillRepository;
        this.changeBillWriteBackService = changeBillWriteBackService;
        this.wipCnBillErrorLogMapper = wipCnBillErrorLogMapper;
    }

    @Subscribe
    @Override
    public void execute(ReqInsProcessNotifyEvent event) {
        ReqInsEntity reqIns = event.getReqInsEntity();
        ChangeBillEntity changeBillEntity = null;
        try {
            changeBillEntity = changeBillRepository.getByReqInsHeaderId(reqIns.getInsHeaderId());
        } catch (RuntimeException re) {
            log.error("指令反查更改单时发生未知异常,msg={}", re.getMessage());
        }
        if (Objects.nonNull(changeBillEntity)) {
            try {
                ChangeBillEntity finalChangeBillEntity = changeBillEntity;
                EntityUtils.retry(() -> changeBillWriteBackService.writeBackToEbs(reqIns, finalChangeBillEntity), 3, "回写EBS更改单");
            } catch (NoOperationRightException ne) {
                String message = ne.getMessage();
                reqIns.setExecuteResult(message);
                reqIns.updateInstruction();
            }
        } else {
            changeBillEntity = ChangeBillEntity.get();
            log.error("指令没有对应有效的更改单,指令ID={},批次={}", reqIns.getInsHeaderId(), reqIns.getAimReqLotNo());
        }
        if (ProcessingStatusEnum.EXCEPTION.getCode().equals(reqIns.getStatus())) {
            // 执行异常, 记录日志
            WipCnBillErrorLogDO errorLogDO = new WipCnBillErrorLogDO();
            errorLogDO.setLogId(UUIDUtils.get32UUID())
                    .setBillId(changeBillEntity.getBillId())
                    .setBillNo(changeBillEntity.getBillNo())
                    .setMoId(changeBillEntity.getMoId())
                    .setMoLotNo(reqIns.getAimReqLotNo())
                    .setOptType(ChangeBillOptTypeEnum.EXECUTE.getCode())
                    .setErrorMessage(reqIns.getExecuteResult())
                    .setStatus(ProcessingStatusEnum.UNHANDLED.getCode());
            EntityUtils.writeStdCrtInfoToEntity(errorLogDO, EntityUtils.getWipUserId());
            wipCnBillErrorLogMapper.insertSelective(errorLogDO);
        }
    }
}
