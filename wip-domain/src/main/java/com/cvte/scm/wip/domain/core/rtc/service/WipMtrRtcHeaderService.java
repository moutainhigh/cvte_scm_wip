package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqItemService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcQueryVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

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

    public WipMtrRtcHeaderService(CheckMtrRtcHeaderService checkMtrRtcHeaderService, WipReqHeaderRepository wipReqHeaderRepository, WipReqItemService wipReqItemService) {
        this.checkMtrRtcHeaderService = checkMtrRtcHeaderService;
        this.wipReqHeaderRepository = wipReqHeaderRepository;
        this.wipReqItemService = wipReqItemService;
    }

    /**
     * 刷新单据
     * @since 2020/9/8 5:52 下午
     * @author xueyuting
     */
    public String refresh(WipMtrRtcHeaderBuildVO wipMtrRtcHeaderBuildVO) {
        WipReqHeaderEntity reqHeaderEntity = wipReqHeaderRepository.selectByMoNo(wipMtrRtcHeaderBuildVO.getMoNo());
        wipMtrRtcHeaderBuildVO.fillMoInfo(reqHeaderEntity);
        // 获取工单工序投料信息
        WipMtrRtcQueryVO wipMtrRtcQueryVO = WipMtrRtcQueryVO.buildForMoUnPost(wipMtrRtcHeaderBuildVO.getOrganizationId(), wipMtrRtcHeaderBuildVO.getMoId(), wipMtrRtcHeaderBuildVO.getHeaderId(),
                wipMtrRtcHeaderBuildVO.getBillType(), wipMtrRtcHeaderBuildVO.getWkpNo(), wipMtrRtcHeaderBuildVO.getItemList());
        List<WipReqItemVO> reqItemVOList = wipReqItemService.getReqItemWithUnPost(wipMtrRtcQueryVO);
        if (ListUtil.empty(reqItemVOList)) {
            throw new ParamsIncorrectException("工单没有可领/退的物料");
        }

        // 校验数量
        checkMtrRtcHeaderService.checkBillQtyUpper(wipMtrRtcHeaderBuildVO.getBillQty(), BigDecimal.valueOf(reqHeaderEntity.getBillQty()));

        WipMtrRtcHeaderEntity rtcHeaderEntity;
        boolean isCreate = true;
        if (StringUtils.isBlank(wipMtrRtcHeaderBuildVO.getHeaderId())) {
            // headerId为空, 生成新的单据
            rtcHeaderEntity = WipMtrRtcHeaderEntity.get();
            rtcHeaderEntity.create(wipMtrRtcHeaderBuildVO);
            wipMtrRtcHeaderBuildVO.setHeaderId(rtcHeaderEntity.getHeaderId());
            // 生成单据行
            rtcHeaderEntity.generateLines(reqItemVOList);
        } else {
            // 刷新单据
            // 校验领料套数
            checkMtrRtcHeaderService.checkBillQtyLower(wipMtrRtcHeaderBuildVO.getBillQty());

            rtcHeaderEntity = WipMtrRtcHeaderEntity.get().getById(wipMtrRtcHeaderBuildVO.getHeaderId());
            if (needRefreshLines(wipMtrRtcHeaderBuildVO, rtcHeaderEntity)) {
                // 若关键信息变更, 则需要重新生成单据行
                rtcHeaderEntity.invalidLines();
                rtcHeaderEntity.generateLines(reqItemVOList);
            } else {
                isCreate = false;
                // 更新子库
                if (StringUtils.isNotBlank(wipMtrRtcHeaderBuildVO.getInvpNo()) && !wipMtrRtcHeaderBuildVO.getInvpNo().equals(rtcHeaderEntity.getInvpNo())) {
                    rtcHeaderEntity.getLineList().forEach(line -> line.setInvpNo(wipMtrRtcHeaderBuildVO.getInvpNo()));
                }
            }
            // 更新单据头
            rtcHeaderEntity.update(wipMtrRtcHeaderBuildVO);
        }
        // 调整单据行
        rtcHeaderEntity.adjustLines(wipMtrRtcHeaderBuildVO, reqItemVOList);

        if (CollectionUtils.isEmpty(rtcHeaderEntity.getLineList().stream().map(WipMtrRtcLineEntity::getLineStatus).filter(WipMtrRtcLineStatusEnum.getUnPostStatus()::contains).collect(Collectors.toSet()))) {
            // 有效行为空
            throw new ParamsIncorrectException("无可领/退的物料");
        }
        // 保存单据
        rtcHeaderEntity.saveLines(isCreate);

        return rtcHeaderEntity.getHeaderId();
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

}
