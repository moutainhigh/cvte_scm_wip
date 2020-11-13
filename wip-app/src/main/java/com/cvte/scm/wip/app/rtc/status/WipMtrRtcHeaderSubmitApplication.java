package com.cvte.scm.wip.app.rtc.status;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.YoNEnum;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.service.CheckMtrRtcHeaderService;
import com.cvte.scm.wip.domain.core.rtc.service.CheckMtrRtcLineService;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcLineService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrInvQtyCheckVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
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
    private CheckMtrRtcHeaderService checkMtrRtcHeaderService;
    private CheckMtrRtcLineService checkMtrRtcLineService;
    private WipMtrRtcHeaderReviewApplication wipMtrRtcHeaderReviewApplication;

    public WipMtrRtcHeaderSubmitApplication(WipMtrRtcLineService wipMtrRtcLineService, CheckMtrRtcHeaderService checkMtrRtcHeaderService, CheckMtrRtcLineService checkMtrRtcLineService, WipMtrRtcHeaderReviewApplication wipMtrRtcHeaderReviewApplication) {
        this.wipMtrRtcLineService = wipMtrRtcLineService;
        this.checkMtrRtcHeaderService = checkMtrRtcHeaderService;
        this.checkMtrRtcLineService = checkMtrRtcLineService;
        this.wipMtrRtcHeaderReviewApplication = wipMtrRtcHeaderReviewApplication;
    }

    public String doAction(String headerId) {
        WipMtrRtcHeaderEntity rtcHeaderEntity = WipMtrRtcHeaderEntity.get().getById(headerId);
        List<WipMtrRtcLineEntity> rtcLineEntityList = rtcHeaderEntity.getLineList();
        WipMtrRtcLineEntity.get().batchGetAssign(rtcLineEntityList);

        checkMtrRtcHeaderService.checkBillQtyLower(rtcHeaderEntity.getBillQty());
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

    public String submitAndReview(String headerId) {
        String msg = this.doAction(headerId);
        WipMtrRtcHeaderReviewDTO wipMtrRtcHeaderReviewDTO = new WipMtrRtcHeaderReviewDTO();
        wipMtrRtcHeaderReviewDTO.setHeaderId(headerId)
                .setApproved(YoNEnum.Y.getCode());
        wipMtrRtcHeaderReviewApplication.doAction(wipMtrRtcHeaderReviewDTO);
        return msg;
    }

}
