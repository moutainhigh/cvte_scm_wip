package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.BatchProcessUtils;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrInvQtyCheckVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/10 16:55
 */
@Service
public class CheckMtrRtcLineService {

    private WipMtrRtcLotControlService wipMtrRtcLotControlService;

    public CheckMtrRtcLineService(WipMtrRtcLotControlService wipMtrRtcLotControlService) {
        this.wipMtrRtcLotControlService = wipMtrRtcLotControlService;
    }

    public void checkChangeable(WipMtrRtcLineBuildVO rtcLineBuildVO, WipMtrRtcLineEntity rtcLine) {
        boolean reqQtyChanged = ListUtil.notEmpty(rtcLine.getAssignList()) && Objects.nonNull(rtcLineBuildVO.getReqQty()) && !rtcLineBuildVO.getReqQty().equals(rtcLine.getReqQty());
        boolean issuedQtyChanged = ListUtil.notEmpty(rtcLine.getAssignList()) && Objects.nonNull(rtcLineBuildVO.getIssuedQty()) && !rtcLineBuildVO.getIssuedQty().equals(rtcLine.getIssuedQty());
        if (reqQtyChanged || issuedQtyChanged) {
            throw new ParamsIncorrectException("分配了批次后无法直接修改行数量,请调整批次分配数量");
        }
    }

    public void checkQtyLower(WipMtrRtcLineBuildVO rtcLineBuildVO) {
        if (rtcLineBuildVO.getReqQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParamsIncorrectException("数量必须大于0");
        }
    }

    public void roundQty(WipMtrRtcLineBuildVO rtcLineBuildVO) {
        Predicate<BigDecimal> isInteger = v -> v.scale() <= 0;
        BigDecimal reqQty = rtcLineBuildVO.getReqQty();
        if (Objects.nonNull(reqQty) && !isInteger.test(reqQty)) {
            rtcLineBuildVO.setReqQty(reqQty.setScale(0, RoundingMode.HALF_UP));
        }
        BigDecimal issuedQty = rtcLineBuildVO.getIssuedQty();
        if (Objects.nonNull(issuedQty) && !isInteger.test(issuedQty)) {
            rtcLineBuildVO.setIssuedQty(issuedQty.setScale(0, RoundingMode.HALF_UP));
        }
    }

    public void checkIssuedQty(WipMtrRtcLineBuildVO rtcLineBuildVO, WipMtrRtcLineEntity rtcLineEntity) {
        BigDecimal issuedQty = rtcLineBuildVO.getIssuedQty();
        BigDecimal reqQty = rtcLineBuildVO.getReqQty();
        if (Objects.nonNull(issuedQty)) {
            if (Objects.isNull(reqQty)) {
                reqQty = rtcLineEntity.getReqQty();
            }
            if (issuedQty.compareTo(reqQty) > 0) {
                throw new ParamsIncorrectException("实发数量不可超过申请数量");
            }
        }
    }

    public void checkQtyUpper(BigDecimal qty, WipReqItemVO reqItemVO, String billType) {
        // 领料or负数物料需求的退料 -- 申请量<=投料单未完成量-有效申请领料单量
        BigDecimal availableQty = reqItemVO.getUnIssuedQty().abs().subtract(reqItemVO.getUnPostQty());
        String errMsg;
        if (WipMtrRtcHeaderTypeEnum.RECEIVE.getCode().equals(billType)) {
            errMsg = "领料数量只能小于可领数量";
        } else {
            // 退料
            errMsg = "退料数量只能小于可退数量";
            if (reqItemVO.getReqQty().compareTo(BigDecimal.ZERO) > 0) {
                // 正数物料需求的退料 -- 申请量<=工单已领数量-已申请数量之和
                availableQty = reqItemVO.getIssuedQty().subtract(reqItemVO.getUnPostQty());
            }
        }
        if (qty.compareTo(availableQty) > 0) {
            throw new ParamsIncorrectException(errMsg + availableQty.toString());
        }
    }

    public void checkInvpNo(String invpNo) {
        if (StringUtils.isBlank(invpNo)) {
            throw new ParamsIncorrectException("子库为空");
        }
    }

    public List<WipMtrInvQtyCheckVO> checkInvQty(List<WipMtrRtcLineEntity> rtcLineEntityList, List<WipMtrSubInvVO> subInvVOList) {
        Map<String, BigDecimal> subInvQtyMap = WipMtrSubInvVO.groupQtyByItemSub(subInvVOList);
        Map<String, BigDecimal> lotQtyMap = WipMtrSubInvVO.groupQtyByItemSubLot(subInvVOList);

        List<WipMtrInvQtyCheckVO> invQtyCheckVOList = new ArrayList<>();
        for (WipMtrRtcLineEntity rtcLine : rtcLineEntityList) {
            try {
                checkInvpNo(rtcLine.getInvpNo());
            } catch (ParamsIncorrectException pe) {
                invQtyCheckVOList.add(WipMtrInvQtyCheckVO.buildItemSub(rtcLine.getItemId(), rtcLine.getItemNo(), rtcLine.getWkpNo(), rtcLine.getInvpNo(), null, null, pe.getMessage()));
                continue;
            }
            List<WipMtrRtcAssignEntity> rtcAssignList = rtcLine.getAssignList();
            BigDecimal supplyQty = subInvQtyMap.get(BatchProcessUtils.getKey(rtcLine.getItemId(), rtcLine.getInvpNo()));
            try {
                // 校验子库存在
                checkSupplyExists(supplyQty);
            } catch (ParamsIncorrectException pe) {
                invQtyCheckVOList.add(WipMtrInvQtyCheckVO.buildItemSub(rtcLine.getItemId(), rtcLine.getItemNo(), rtcLine.getWkpNo(), rtcLine.getInvpNo(), supplyQty, supplyQty, pe.getMessage()));
                continue;
            }
            if (ListUtil.notEmpty(rtcAssignList)) {
                // 校验分配的批次现有量
                for (WipMtrRtcAssignEntity rtcAssign : rtcAssignList) {
                    try {
                        supplyQty = lotQtyMap.get(BatchProcessUtils.getKey(rtcLine.getItemId(), rtcAssign.getInvpNo(), rtcAssign.getMtrLotNo()));
                        checkQty(rtcAssign.getIssuedQty(), supplyQty);
                    } catch (ParamsIncorrectException pe) {
                        invQtyCheckVOList.add(WipMtrInvQtyCheckVO.buildItemSubLot(rtcLine.getItemId(), rtcLine.getItemNo(), rtcLine.getWkpNo(), rtcLine.getInvpNo(), rtcAssign.getMtrLotNo(), supplyQty, supplyQty, pe.getMessage()));
                    }
                }
            } else {
                // 校验子库现有量
                try {
                    checkQty(rtcLine.getIssuedQty(), supplyQty);
                } catch (ParamsIncorrectException pe) {
                    invQtyCheckVOList.add(WipMtrInvQtyCheckVO.buildItemSub(rtcLine.getItemId(), rtcLine.getItemNo(), rtcLine.getWkpNo(), rtcLine.getInvpNo(), supplyQty, supplyQty, pe.getMessage()));
                }
            }
        }
        return invQtyCheckVOList;
    }

    public void checkLotControl(WipMtrRtcHeaderEntity rtcHeaderEntity) {
        List<WipMtrRtcLineEntity> rtcLineList = rtcHeaderEntity.getLineList();
        List<String> itemIdList = rtcLineList.stream().map(WipMtrRtcLineEntity::getItemId).collect(Collectors.toList());
        List<String> controlItemIdList = wipMtrRtcLotControlService.getLotControlItem(rtcHeaderEntity.getOrganizationId(), itemIdList);
        StringBuilder errMsgBuilder = new StringBuilder();
        for (WipMtrRtcLineEntity rtcLine : rtcLineList) {
            List<WipMtrRtcAssignEntity> assignEntityList = rtcLine.getAssignList().stream().filter(assign -> StatusEnum.NORMAL.getCode().equals(assign.getAssignStatus())).collect(Collectors.toList());
            if (controlItemIdList.contains(rtcLine.getItemId()) && ListUtil.empty(assignEntityList)) {
                errMsgBuilder.append("物料").append(rtcLine.getItemNo()).append("启用批次管控,必须分配批次;");
            }
            if (ListUtil.notEmpty(assignEntityList)) {
                List<String> qtyErrMsgList = new ArrayList<>();
                // 校验申请数量 = 批次分配数量之和
                BigDecimal assignQty = assignEntityList.stream()
                        .map(WipMtrRtcAssignEntity::getAssignQty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (rtcLine.getReqQty().compareTo(assignQty) != 0) {
                    qtyErrMsgList.add("申请数量必须与批次分配数量相等");
                }
                // 校验实发数量 = 批次实发数量之和
                BigDecimal issuedQty = assignEntityList.stream()
                        .map(WipMtrRtcAssignEntity::getIssuedQty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (rtcLine.getIssuedQty().compareTo(issuedQty) != 0) {
                    qtyErrMsgList.add("实发数量必须与批次实发数量相等");
                }
                if (ListUtil.notEmpty(qtyErrMsgList)) {
                    errMsgBuilder.append("物料").append(rtcLine.getItemNo()).append(String.join(",", qtyErrMsgList));
                }
            }
        }
        if (errMsgBuilder.length() > 0) {
            throw new ParamsIncorrectException(errMsgBuilder.toString());
        }
    }

    public void checkLineCanPost(String[] lineIds) {
        List<WipMtrRtcLineEntity> rtcLineList = WipMtrRtcLineEntity.get().getByLineIds(lineIds);
        List<WipMtrRtcLineEntity> alreadyPostLineList = rtcLineList.stream().filter(line -> !WipMtrRtcLineStatusEnum.getUnPostStatus().contains(line.getLineStatus())).collect(Collectors.toList());
        if (ListUtil.notEmpty(alreadyPostLineList)) {
            StringBuilder errMsg = new StringBuilder();
            for (WipMtrRtcLineEntity alreadyPostLine : alreadyPostLineList) {
                WipMtrRtcLineStatusEnum rtcLineStatusEnum = CodeableEnumUtils.getCodeableEnumByCode(alreadyPostLine.getLineStatus(), WipMtrRtcLineStatusEnum.class);
                errMsg.append("物料").append(alreadyPostLine.getItemNo()).append("的状态为").append(rtcLineStatusEnum.getDesc()).append(",无法提交过账;");
            }
            throw new ParamsIncorrectException(errMsg.toString());
        }
    }

    public void checkLineCanEdit(WipMtrRtcLineBuildVO rtcLineBuildVO, WipMtrRtcLineEntity rtcLine) {
        if (StringUtils.isNotBlank(rtcLineBuildVO.getInvpNo()) && StringUtils.isNotBlank(rtcLine.getInvpNo()) && !rtcLineBuildVO.getInvpNo().equals(rtcLine.getInvpNo())) {
            if (ListUtil.notEmpty(rtcLine.getAssignList())) {
                throw new ParamsIncorrectException("批次已分配, 请先删除批次");
            }
        }
    }

    private void checkSupplyExists(BigDecimal supplyQty) {
        if (Objects.isNull(supplyQty)) {
            throw new ParamsIncorrectException("查不到该子库的现有量");
        }
    }

    private void checkQty(BigDecimal sourceQty, BigDecimal supplyQty) {
        if (sourceQty.compareTo(supplyQty) > 0) {
            throw new ParamsIncorrectException("现有量不足");
        }
    }

}
