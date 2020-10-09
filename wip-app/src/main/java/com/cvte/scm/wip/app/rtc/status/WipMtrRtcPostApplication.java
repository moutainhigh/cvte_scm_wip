package com.cvte.scm.wip.app.rtc.status;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.service.CheckMtrRtcLineService;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcHeaderService;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcLineService;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcWriteBackService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/29 11:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class WipMtrRtcPostApplication {

    private WipMtrRtcLineService wipMtrRtcLineService;
    private CheckMtrRtcLineService checkMtrRtcLineService;
    private WipMtrRtcWriteBackService wipMtrRtcWriteBackService;
    private WipMtrRtcHeaderService wipMtrRtcHeaderService;

    public WipMtrRtcPostApplication(WipMtrRtcLineService wipMtrRtcLineService, CheckMtrRtcLineService checkMtrRtcLineService, WipMtrRtcWriteBackService wipMtrRtcWriteBackService, WipMtrRtcHeaderService wipMtrRtcHeaderService) {
        this.wipMtrRtcLineService = wipMtrRtcLineService;
        this.checkMtrRtcLineService = checkMtrRtcLineService;
        this.wipMtrRtcWriteBackService = wipMtrRtcWriteBackService;
        this.wipMtrRtcHeaderService = wipMtrRtcHeaderService;
    }

    public void doAction(String headerId) {
        // 获取头
        WipMtrRtcHeaderEntity rtcHeader = WipMtrRtcHeaderEntity.get().getById(headerId);
        // 获取未过账的行
        rtcHeader.getLineList().removeIf(line -> !WipMtrRtcLineStatusEnum.getUnPostStatus().contains(line.getLineStatus()));

        this.post(rtcHeader);
    }

    public void doAction(String[] lineIds) {
        // 获取行
        List<WipMtrRtcLineEntity> rtcLineList = WipMtrRtcLineEntity.get().getByLineIds(lineIds);
        WipMtrRtcHeaderEntity rtcHeader = WipMtrRtcHeaderEntity.get().getById(rtcLineList.get(0).getHeaderId());
        rtcHeader.setLineList(rtcLineList);

        this.post(rtcHeader);
    }

    private void post(WipMtrRtcHeaderEntity rtcHeader) {
        if (ListUtil.empty(rtcHeader.getLineList())) {
            log.info("无可过账的领退料行, headerId = {}", rtcHeader.getHeaderId());
            return;
        }
        // 校验现有量
        wipMtrRtcLineService.validateItemInvQty(rtcHeader);
        // 校验行状态
        checkMtrRtcLineService.checkLineCanPost(rtcHeader.getLineList().stream().map(WipMtrRtcLineEntity::getLineId).toArray(String[]::new));
        // 提交过账
        wipMtrRtcWriteBackService.post(rtcHeader);
        // 过账成功处理
        wipMtrRtcHeaderService.post(rtcHeader);
    }

}
