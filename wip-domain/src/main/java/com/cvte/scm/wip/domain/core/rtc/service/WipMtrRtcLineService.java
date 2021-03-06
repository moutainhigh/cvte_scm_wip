package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.ExecutionResultEnum;
import com.cvte.scm.wip.common.utils.BatchProcessUtils;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqItemService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrSubInvRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.*;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderStatusEnum;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/10 14:33
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class WipMtrRtcLineService {

    private WipReqItemService wipReqItemService;
    private CheckMtrRtcLineService checkMtrRtcLineService;
    private WipMtrSubInvRepository wipMtrSubInvRepository;
    private WipMtrRtcLineRepository wipMtrRtcLineRepository;
    private WipMtrRtcWriteBackService wipMtrRtcWriteBackService;

    public WipMtrRtcLineService(WipReqItemService wipReqItemService, CheckMtrRtcLineService checkMtrRtcLineService, WipMtrSubInvRepository wipMtrSubInvRepository, WipMtrRtcLineRepository wipMtrRtcLineRepository, WipMtrRtcWriteBackService wipMtrRtcWriteBackService) {
        this.wipReqItemService = wipReqItemService;
        this.checkMtrRtcLineService = checkMtrRtcLineService;
        this.wipMtrSubInvRepository = wipMtrSubInvRepository;
        this.wipMtrRtcLineRepository = wipMtrRtcLineRepository;
        this.wipMtrRtcWriteBackService = wipMtrRtcWriteBackService;
    }

    public List<WipMtrInvQtyCheckVO> batchUpdate(List<WipMtrRtcLineBuildVO> rtcLineBuildVOList) {
        String[] lineIdList = rtcLineBuildVOList.stream().map(WipMtrRtcLineBuildVO::getLineId).toArray(String[]::new);
        // 查询单据行
        List<WipMtrRtcLineEntity> rtcLineList = WipMtrRtcLineEntity.get().getByLineIds(lineIdList);
        WipMtrRtcLineEntity.get().batchGetAssign(rtcLineList);
        Map<String, WipMtrRtcLineEntity> rtcLineMap = rtcLineList.stream().collect(Collectors.toMap(WipMtrRtcLineEntity::getUniqueId, Function.identity()));

        // 查询单据头
        WipMtrRtcHeaderEntity rtcHeader = getHeader(rtcLineBuildVOList);
        rtcHeader.setLineList(rtcLineList);
        // 查询工单投料信息
        List<WipReqItemVO> reqItemVOList = getItemVOList(rtcHeader, rtcLineList);
        Map<String, WipReqItemVO> reqItemVOMap = reqItemVOList.stream().collect(Collectors.toMap(WipReqItemVO::getKey, Function.identity()));

        List<WipMtrInvQtyCheckVO> invQtyCheckVOS = new ArrayList<>();
        for (WipMtrRtcLineBuildVO rtcLineBuildVO : rtcLineBuildVOList) {
            WipMtrRtcLineEntity rtcLine = rtcLineMap.get(rtcLineBuildVO.getLineId());
            WipReqItemVO reqItemVO = reqItemVOMap.get(rtcLine.getReqKey(rtcHeader.getMoId()));
            try {
                checkMtrRtcLineService.checkChangeable(rtcLineBuildVO, rtcLine);
                checkMtrRtcLineService.checkInvpNo(rtcLineBuildVO.getInvpNo());
                // 校验数量下限
                checkMtrRtcLineService.checkQtyLower(rtcLineBuildVO);
                // 取整
                checkMtrRtcLineService.roundQty(rtcLineBuildVO);
                // 校验实发数量
                checkMtrRtcLineService.checkIssuedQty(rtcLineBuildVO, rtcLine);
                // 限制数量
                if (Objects.nonNull(rtcLineBuildVO.getReqQty())) {
                    checkMtrRtcLineService.checkQtyUpper(rtcLineBuildVO.getReqQty(), reqItemVO, rtcHeader.getBillType());
                }
                if (Objects.nonNull(rtcLineBuildVO.getIssuedQty())) {
                    checkMtrRtcLineService.checkQtyUpper(rtcLineBuildVO.getIssuedQty(), reqItemVO, rtcHeader.getBillType());
                }
                checkMtrRtcLineService.checkLineCanEdit(rtcLineBuildVO, rtcLine);
            } catch (ParamsIncorrectException pe) {
                invQtyCheckVOS.add(WipMtrInvQtyCheckVO.buildItemSub(rtcLine.getItemId(), rtcLine.getItemNo(), rtcLine.getWkpNo(), rtcLine.getInvpNo(), null, null, pe.getMessage()));
                continue;
            }
            // 用参数更新单据行的字段
            rtcLine.update(rtcLineBuildVO);
        }
        if (ListUtil.notEmpty(invQtyCheckVOS)) {
            // 设置提醒类型为 失败
            invQtyCheckVOS.forEach(checkVO -> checkVO.setNoticeType(ExecutionResultEnum.FAILED.getCode()));
            return invQtyCheckVOS;
        }
        // 校验现有量
        invQtyCheckVOS = validateItemInvQty(rtcHeader);
        if (ListUtil.notEmpty(invQtyCheckVOS)) {
            // 获取物料已申请未过账数量
            List<WipReqItemVO> unPostReqItemVOList = getReqItem(rtcHeader, invQtyCheckVOS.stream().map(WipMtrInvQtyCheckVO::getItemId).collect(Collectors.toList()));
            Map<String, BigDecimal> unPostReqItemVOMap = WipReqItemVO.groupUnPostQtyByItemSub(unPostReqItemVOList);
            for (WipMtrInvQtyCheckVO invQtyCheckVO : invQtyCheckVOS) {
                String subInvKey = BatchProcessUtils.getKey(invQtyCheckVO.getItemId(), invQtyCheckVO.getInvpNo());
                BigDecimal unPostQty = unPostReqItemVOMap.get(subInvKey);
                if (Objects.nonNull(unPostQty) && Objects.nonNull(invQtyCheckVO.getInvQty())) {
                    // 设置可用量 = 现有量 - 物料已申请未过账数量
                    invQtyCheckVO.setAvailQty(invQtyCheckVO.getInvQty().subtract(unPostQty));
                }
                // 设置提醒类型为 成功
                invQtyCheckVO.setNoticeType(ExecutionResultEnum.SUCCESS.getCode());
            }
        }
        for (WipMtrRtcLineEntity updateLine : rtcLineList) {
            // 更新行写库
            updateLine.update();
        }
        if (WipMtrRtcHeaderStatusEnum.effective(rtcHeader.getBillStatus())) {
            // 审核通过或部分过账, 更新后需同步到EBS
            wipMtrRtcWriteBackService.update(rtcHeader);
        }
        return invQtyCheckVOS;
    }

    public List<WipMtrInvQtyCheckVO> validateItemInvQty(WipMtrRtcHeaderEntity rtcHeaderEntity) {
        List<WipMtrRtcLineEntity> rtcLineEntityList = rtcHeaderEntity.getLineList();
        if (WipMtrRtcHeaderTypeEnum.RETURN.getCode().equals(rtcHeaderEntity.getBillType()) || ListUtil.empty(rtcLineEntityList)) {
            // 类型为退料单 或 领料行为空时不校验现有量
            return Collections.emptyList();
        }

        List<WipMtrSubInvVO> subInvQueryList = new ArrayList<>();
        for (WipMtrRtcLineEntity rtcLineEntity : rtcLineEntityList) {
            List<WipMtrRtcAssignEntity> rtcAssignEntityList = rtcLineEntity.getAssignList();
            if (ListUtil.notEmpty(rtcAssignEntityList)) {
                // 有批次分配时的考虑批次的现有量
                for (WipMtrRtcAssignEntity rtcAssignEntity : rtcAssignEntityList) {
                    WipMtrSubInvVO subInvQueryVO = new WipMtrSubInvVO();
                    subInvQueryVO.setInventoryItemId(rtcLineEntity.getItemId())
                            .setSubinventoryCode(rtcAssignEntity.getInvpNo())
                            .setLotNumber(rtcAssignEntity.getMtrLotNo());
                    subInvQueryList.add(subInvQueryVO);
                }
            } else {
                // 考虑子库现有量
                WipMtrSubInvVO subInvQueryVO = new WipMtrSubInvVO();
                subInvQueryVO.setInventoryItemId(rtcLineEntity.getItemId())
                        .setSubinventoryCode(rtcLineEntity.getInvpNo());
                subInvQueryList.add(subInvQueryVO);
            }
        }
        subInvQueryList.get(0).setOrganizationId(rtcHeaderEntity.getOrganizationId())
                .setFactoryId(rtcHeaderEntity.getFactoryId());
        List<WipMtrSubInvVO> subInvVOList = wipMtrSubInvRepository.selectByVO(subInvQueryList);

        return checkMtrRtcLineService.checkInvQty(rtcLineEntityList, subInvVOList);
    }

    private WipMtrRtcHeaderEntity getHeader(List<WipMtrRtcLineBuildVO> rtcLineModifyVOList) {
        String headerId = rtcLineModifyVOList.get(0).getHeaderId();
        return WipMtrRtcHeaderEntity.get().getById(headerId);
    }

    private List<WipReqItemVO> getItemVOList(WipMtrRtcHeaderEntity rtcHeaderEntity, List<WipMtrRtcLineEntity> rtcLineEntityList) {
        List<String> itemKeyList = rtcLineEntityList.stream().map(WipMtrRtcLineEntity::getItemId).collect(Collectors.toList());
        WipMtrRtcQueryVO wipMtrRtcQueryVO = WipMtrRtcQueryVO.buildForMoUnPost(rtcHeaderEntity.getOrganizationId(), rtcHeaderEntity.getMoId(), rtcHeaderEntity.getHeaderId(),
                rtcHeaderEntity.getBillType(), rtcHeaderEntity.getWkpNo(), itemKeyList);
        return wipReqItemService.getReqItemWithUnPost(wipMtrRtcQueryVO);
    }

    private List<WipReqItemVO> getReqItem(WipMtrRtcHeaderEntity rtcHeader, List<String> itemIdList) {
        WipMtrRtcQueryVO wipMtrRtcQueryVO = WipMtrRtcQueryVO.buildForItemUnPost(rtcHeader.getOrganizationId(), rtcHeader.getFactoryId(), rtcHeader.getHeaderId(), rtcHeader.getBillType(), itemIdList);
        return wipMtrRtcLineRepository.batchSumItemUnPostQty(wipMtrRtcQueryVO);
    }

}
