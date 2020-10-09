package com.cvte.scm.wip.app.rtc.status;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/21 10:29
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcLineCancelApplication {

    public void doAction(String[] lineIds) {
        List<WipMtrRtcLineEntity> rtcLineEntityList = WipMtrRtcLineEntity.get().getByLineIds(lineIds);
        WipMtrRtcHeaderEntity rtcHeaderEntity = WipMtrRtcHeaderEntity.get().getById(rtcLineEntityList.get(0).getHeaderId());
        rtcHeaderEntity.checkCancelable();
        if (WipMtrRtcHeaderStatusEnum.EFFECTIVE.getCode().equals(rtcHeaderEntity.getBillStatus())) {
            throw new ParamsIncorrectException("已审核的单据不能按行取消");
        }
        WipMtrRtcLineEntity.get().batchCancel(rtcLineEntityList);
    }

}
