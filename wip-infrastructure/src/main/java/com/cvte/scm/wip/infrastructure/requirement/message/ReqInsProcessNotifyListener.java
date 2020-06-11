package com.cvte.scm.wip.infrastructure.requirement.message;

import com.cvte.csb.core.exception.client.forbiddens.NoOperationRightException;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.EventListener;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
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

    private ChangeBillWriteBackService changeBillWriteBackService;
    private WipCnBillErrorLogMapper wipCnBillErrorLogMapper;

    public ReqInsProcessNotifyListener(ChangeBillWriteBackService changeBillWriteBackService, WipCnBillErrorLogMapper wipCnBillErrorLogMapper) {
        this.changeBillWriteBackService = changeBillWriteBackService;
        this.wipCnBillErrorLogMapper = wipCnBillErrorLogMapper;
    }

    @Subscribe
    @Override
    public void execute(ReqInsProcessNotifyEvent event) {
        ReqInsEntity reqIns = event.getReqInsEntity();
        List<String> billNoList = new ArrayList<>();
        billNoList.add(reqIns.getSourceChangeBillId());
        ChangeBillEntity changeBillEntity = ChangeBillEntity.get().getById(billNoList).get(0);
        try {
            EntityUtils.retry(() -> changeBillWriteBackService.writeBackToEbs(reqIns, changeBillEntity), 3, "回写EBS更改单");
        } catch (NoOperationRightException ne) {
            String message = ne.getMessage();
            reqIns.setExecuteResult(message);
            reqIns.updateInstruction();
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
