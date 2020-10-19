package com.cvte.scm.wip.app.rtc.status;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.service.CheckMtrRtcLineService;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcLineService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrInvQtyCheckVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 15:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcHeaderSubmitApplication {

    private WipMtrRtcLineService wipMtrRtcLineService;
    private CheckMtrRtcLineService checkMtrRtcLineService;

    public WipMtrRtcHeaderSubmitApplication(WipMtrRtcLineService wipMtrRtcLineService, CheckMtrRtcLineService checkMtrRtcLineService) {
        this.wipMtrRtcLineService = wipMtrRtcLineService;
        this.checkMtrRtcLineService = checkMtrRtcLineService;
    }

    public String doAction(String headerId) {
        WipMtrRtcHeaderEntity rtcHeaderEntity = WipMtrRtcHeaderEntity.get().getById(headerId);
        List<WipMtrRtcLineEntity> rtcLineEntityList = rtcHeaderEntity.getLineList();
        WipMtrRtcLineEntity.get().batchGetAssign(rtcLineEntityList);

        String msg = null;
        List<WipMtrInvQtyCheckVO> invQtyCheckVOS = wipMtrRtcLineService.validateItemInvQty(rtcHeaderEntity);
        checkMtrRtcLineService.checkLotControl(rtcHeaderEntity);
        if (ListUtil.notEmpty(invQtyCheckVOS)) {
            // 提交时允许现有量不足, 提前打单的场景
            msg = WipMtrInvQtyCheckVO.buildMsg(invQtyCheckVOS);
        }

        rtcHeaderEntity.submit();
        return msg;
    }

}
