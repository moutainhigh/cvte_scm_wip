package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqItemService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotIssuedService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/8 17:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class WipMtrRtcHeaderService {

    private CheckMtrRtcHeaderService checkMtrRtcHeaderService;
    private WipReqHeaderRepository wipReqHeaderRepository;
    private WipReqItemService wipReqItemService;
    private WipReqLotIssuedService wipReqLotIssuedService;

    public WipMtrRtcHeaderService(CheckMtrRtcHeaderService checkMtrRtcHeaderService, WipReqHeaderRepository wipReqHeaderRepository, WipReqItemService wipReqItemService, WipReqLotIssuedService wipReqLotIssuedService) {
        this.checkMtrRtcHeaderService = checkMtrRtcHeaderService;
        this.wipReqHeaderRepository = wipReqHeaderRepository;
        this.wipReqItemService = wipReqItemService;
        this.wipReqLotIssuedService = wipReqLotIssuedService;
    }

    /**
     * 刷新单据
     * @since 2020/9/8 5:52 下午
     * @author xueyuting
     */
    public String refresh(WipMtrRtcHeaderBuildVO wipMtrRtcHeaderBuildVO) {
        WipReqHeaderEntity reqHeaderEntity = wipReqHeaderRepository.selectByMoNo(wipMtrRtcHeaderBuildVO.getMoNo());
        wipMtrRtcHeaderBuildVO.fillMoInfo(reqHeaderEntity);
        // 校验工单状态
        checkMtrRtcHeaderService.checkBillStatus(reqHeaderEntity.getBillStatus());
        // 校验OCS订单取消
        checkMtrRtcHeaderService.checkOcsCanceled(wipMtrRtcHeaderBuildVO.getMoId());

        // 获取工单工序投料信息
        WipMtrRtcQueryVO wipMtrRtcQueryVO = WipMtrRtcQueryVO.buildForMoUnPost(wipMtrRtcHeaderBuildVO.getOrganizationId(), wipMtrRtcHeaderBuildVO.getMoId(), wipMtrRtcHeaderBuildVO.getHeaderId(),
                wipMtrRtcHeaderBuildVO.getBillType(), wipMtrRtcHeaderBuildVO.getWkpNo(), wipMtrRtcHeaderBuildVO.getItemList());
        List<WipReqItemVO> reqItemVOList = wipReqItemService.getReqItemWithUnPost(wipMtrRtcQueryVO);
        if (ListUtil.empty(reqItemVOList)) {
            throw new ParamsIncorrectException("工单没有可领/退的物料");
        }

        // 校验数量
        checkMtrRtcHeaderService.checkBillQtyUpper(wipMtrRtcHeaderBuildVO.getBillQty(), BigDecimal.valueOf(reqHeaderEntity.getBillQty()));

        WipMtrRtcHeaderEntity rtcHeader;
        boolean isCreate = true;
        if (StringUtils.isBlank(wipMtrRtcHeaderBuildVO.getHeaderId())) {
            // headerId为空, 生成新的单据
            rtcHeader = WipMtrRtcHeaderEntity.get();
            rtcHeader.create(wipMtrRtcHeaderBuildVO);
            wipMtrRtcHeaderBuildVO.setHeaderId(rtcHeader.getHeaderId());
            // 生成单据行
            rtcHeader.generateLines(reqItemVOList);
        } else {
            isCreate = false;
            // 刷新单据
            // 校验领料套数
            checkMtrRtcHeaderService.checkBillQtyLower(wipMtrRtcHeaderBuildVO.getBillQty());

            rtcHeader = WipMtrRtcHeaderEntity.get().getById(wipMtrRtcHeaderBuildVO.getHeaderId());
            if (needRefreshLines(wipMtrRtcHeaderBuildVO, rtcHeader)) {
                // 若关键信息变更, 则需要重新生成单据行
                rtcHeader.invalidLines();
                rtcHeader.generateLines(reqItemVOList);
            } else {
                // 更新子库
                if (StringUtils.isNotBlank(wipMtrRtcHeaderBuildVO.getInvpNo()) && !wipMtrRtcHeaderBuildVO.getInvpNo().equals(rtcHeader.getInvpNo())) {
                    rtcHeader.getLineList().forEach(line -> line.setInvpNo(wipMtrRtcHeaderBuildVO.getInvpNo()));
                }
            }
            // 更新单据头
            rtcHeader.update(wipMtrRtcHeaderBuildVO);
        }
        // 调整单据行
        rtcHeader.adjustLines(wipMtrRtcHeaderBuildVO, reqItemVOList);
        // 自动分配批次
        List<WipMtrRtcLineEntity> rtcLineList = rtcHeader.getLineList();
        WipMtrRtcLineEntity.get().batchGetAssign(rtcLineList);
        List<WipMtrRtcAssignEntity> rtcAssignList = new ArrayList<>();
        for (WipMtrRtcLineEntity rtcLine : rtcLineList) {
            rtcAssignList.addAll(rtcLine.generateAssign(rtcHeader.getMoId(), rtcHeader.getFactoryId()));
        }

        checkMtrRtcHeaderService.checkReqFinished(rtcHeader);
        // 保存单据
        rtcHeader.save(isCreate);
        rtcHeader.saveLines(isCreate);
        if (ListUtil.notEmpty(rtcAssignList)) {
            WipMtrRtcLineEntity.get().createAssigns(rtcAssignList);
        }

        return rtcHeader.getHeaderId();
    }

    public void post(WipMtrRtcHeaderEntity rtcHeader) {
        // 更新行状态
        WipMtrRtcLineEntity.get().batchPost(rtcHeader.getLineList());
        // 更新头状态
        rtcHeader.post();
        // 更新批次领料数量
        List<WipReqLotIssuedEntity> lotIssuedList = generateIssuedLot(rtcHeader);
        if (ListUtil.notEmpty(lotIssuedList)) {
            wipReqLotIssuedService.issue(lotIssuedList);
        }
    }

    /**
     * 判断是否需要重新生成领退料行
     * @since 2020/9/9 10:01 上午
     * @author xueyuting
     */
    private boolean needRefreshLines(WipMtrRtcHeaderBuildVO headerBuildVO, WipMtrRtcHeaderEntity headerEntity) {
        BiPredicate<String, String> valueChanged = (p, v) -> StringUtils.isNotBlank(p) && !p.equals(v);
        // 工单或工序变更时需要重新生成
        return valueChanged.test(headerBuildVO.getMoId(), headerEntity.getMoId())
                || valueChanged.test(headerBuildVO.getWkpNo(), headerEntity.getWkpNo());
    }

    private List<WipReqLotIssuedEntity> generateIssuedLot(WipMtrRtcHeaderEntity rtcHeader) {
        List<WipReqLotIssuedEntity> lotIssuedList = new ArrayList<>();
        for (WipMtrRtcLineEntity rtcLine : rtcHeader.getLineList()) {
            if (ListUtil.notEmpty(rtcLine.getAssignList())) {
                for (WipMtrRtcAssignEntity rtcAssign : rtcLine.getAssignList()) {
                    WipReqLotIssuedEntity lotIssued = WipReqLotIssuedEntity.buildForPost(rtcHeader.getOrganizationId(), rtcHeader.getMoId(), rtcLine.getItemNo(), rtcLine.getWkpNo(), rtcAssign.getMtrLotNo(), rtcAssign.getIssuedQty());
                    lotIssuedList.add(lotIssued);
                }
            } else if (StringUtils.isNotBlank(rtcLine.getMoLotNo())) {
                WipReqLotIssuedEntity lotIssued = WipReqLotIssuedEntity.buildForPost(rtcHeader.getOrganizationId(), rtcHeader.getMoId(), rtcLine.getItemNo(), rtcLine.getWkpNo(), rtcLine.getMoLotNo(), rtcLine.getIssuedQty());
                lotIssuedList.add(lotIssued);
            }
        }
        return lotIssuedList;
    }

}
