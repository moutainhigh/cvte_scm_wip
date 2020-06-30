package com.cvte.scm.wip.infrastructure.changebill.message;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.EventListener;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.event.ChangeBillParseErrorEvent;
import com.cvte.scm.wip.domain.core.changebill.valueobject.enums.ChangeBillOptTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqHeaderService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import com.cvte.scm.wip.infrastructure.changebill.mapper.WipCnBillErrorLogMapper;
import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillErrorLogDO;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/11 17:41
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ChangeBillParseErrorListener implements EventListener<ChangeBillParseErrorEvent> {

    private WipCnBillErrorLogMapper wipCnBillErrorLogMapper;
    private WipReqHeaderService wipReqHeaderService;

    public ChangeBillParseErrorListener(WipCnBillErrorLogMapper wipCnBillErrorLogMapper, WipReqHeaderService wipReqHeaderService) {
        this.wipCnBillErrorLogMapper = wipCnBillErrorLogMapper;
        this.wipReqHeaderService = wipReqHeaderService;
    }

    @Subscribe
    @Override
    public void execute(ChangeBillParseErrorEvent event) {
        ChangeBillEntity changeBillEntity = event.getChangeBillEntity();
        String errorMassage = event.getErrorMessage();

        WipCnBillErrorLogDO errorLogDO = new WipCnBillErrorLogDO();
        errorLogDO.setLogId(UUIDUtils.get32UUID())
                .setBillId(changeBillEntity.getBillId())
                .setBillNo(changeBillEntity.getBillNo())
                .setMoId(changeBillEntity.getMoId())
                .setOptType(ChangeBillOptTypeEnum.SYNC.getCode())
                .setErrorMessage(errorMassage)
                .setStatus(ProcessingStatusEnum.PENDING.getCode());
        WipReqHeaderEntity reqHeader = wipReqHeaderService.getBySourceId(changeBillEntity.getMoId());
        if (Objects.nonNull(reqHeader)) {
            errorLogDO.setMoLotNo(reqHeader.getSourceLotNo());
        }

        EntityUtils.writeStdCrtInfoToEntity(errorLogDO, EntityUtils.getWipUserId());
        wipCnBillErrorLogMapper.insertSelective(errorLogDO);
    }

}
