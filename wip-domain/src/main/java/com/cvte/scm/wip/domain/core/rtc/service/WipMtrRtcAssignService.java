package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcAssignBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/11 09:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class WipMtrRtcAssignService {

    private WipMtrRtcWriteBackService wipMtrRtcWriteBackService;

    public WipMtrRtcAssignService(WipMtrRtcWriteBackService wipMtrRtcWriteBackService) {
        this.wipMtrRtcWriteBackService = wipMtrRtcWriteBackService;
    }

    public String batchSave(List<WipMtrRtcAssignBuildVO> rtcAssignBuildVOList) {
        List<WipMtrRtcAssignEntity> createAssignEntityList = new ArrayList<>();

        List<String> updateLineIdList = new ArrayList<>();
        List<WipMtrRtcAssignEntity> deleteAssignEntityList = new ArrayList<>();
        Iterator<WipMtrRtcAssignBuildVO> iterator = rtcAssignBuildVOList.iterator();
        StringBuilder errMsgBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            WipMtrRtcAssignBuildVO rtcAssignBuildVO = iterator.next();
            String assignId = rtcAssignBuildVO.getAssignId();
            if (ChangedTypeEnum.ADD.getCode().equals(rtcAssignBuildVO.getChangeType())) {
                if (StringUtils.isNotBlank(rtcAssignBuildVO.getAssignId())) {
                    errMsgBuilder.append(rtcAssignBuildVO.getMtrLotNo()).append("新增时唯一标识必须为空;");
                }
                // 新增实体
                WipMtrRtcAssignEntity rtcAssign = WipMtrRtcAssignEntity.get();
                rtcAssign.create(rtcAssignBuildVO);
                createAssignEntityList.add(rtcAssign);

                iterator.remove();
            } else if (ChangedTypeEnum.DELETE.getCode().equals(rtcAssignBuildVO.getChangeType())) {
                if (StringUtils.isBlank(rtcAssignBuildVO.getAssignId())) {
                    errMsgBuilder.append(rtcAssignBuildVO.getMtrLotNo()).append("删除时唯一标识不能为空;");
                }
                WipMtrRtcAssignEntity deleteAssignEntity = WipMtrRtcAssignEntity.get();
                deleteAssignEntity.setAssignId(rtcAssignBuildVO.getAssignId());
                deleteAssignEntityList.add(deleteAssignEntity);

                iterator.remove();
            } else if (ChangedTypeEnum.UPDATE.getCode().equals(rtcAssignBuildVO.getChangeType())) {
                if (StringUtils.isBlank(rtcAssignBuildVO.getAssignId())) {
                    errMsgBuilder.append(rtcAssignBuildVO.getMtrLotNo()).append("更新时唯一标识不能为空;");
                }
                updateLineIdList.add(assignId);
            } else {
                errMsgBuilder.append(rtcAssignBuildVO.getMtrLotNo());
                if (StringUtils.isBlank(rtcAssignBuildVO.getChangeType())) {
                    errMsgBuilder.append("变更类型不可为空;");
                } else {
                    errMsgBuilder.append("非法的变更类型").append(rtcAssignBuildVO.getChangeType()).append(";");
                }
            }
        }
        if (errMsgBuilder.length() > 0) {
            throw new ParamsIncorrectException(errMsgBuilder.toString());
        }

        WipMtrRtcLineEntity rtcLine = WipMtrRtcLineEntity.get();
        List<WipMtrRtcAssignEntity> allAssignEntityList = new ArrayList<>();
        List<WipMtrRtcAssignEntity> updateAssignEntityList = new ArrayList<>();
        if (ListUtil.notEmpty(updateLineIdList)) {
            // 更新实体
            List<WipMtrRtcAssignEntity> rtcAssignList = WipMtrRtcAssignEntity.get().getByIds(updateLineIdList.toArray(new String[0]));
            Map<String, WipMtrRtcAssignEntity> rtcAssignMap = rtcAssignList.stream().collect(Collectors.toMap(WipMtrRtcAssignEntity::getUniqueId, Function.identity()));
            for (WipMtrRtcAssignBuildVO rtcAssignBuildVO : rtcAssignBuildVOList) {
                WipMtrRtcAssignEntity rtcAssign = rtcAssignMap.get(rtcAssignBuildVO.getAssignId());
                rtcAssign.update(rtcAssignBuildVO);
                updateAssignEntityList.add(rtcAssign);
            }
            // 批量更新
            if (ListUtil.notEmpty(updateAssignEntityList)) {
                rtcLine.updateAssigns(updateAssignEntityList);
                allAssignEntityList.addAll(updateAssignEntityList);
            }
        }
        if (ListUtil.notEmpty(deleteAssignEntityList)) {
            // 批量删除
            rtcLine.deleteAssigns(deleteAssignEntityList);
        }
        if (ListUtil.notEmpty(createAssignEntityList)) {
            // 批量创建
            rtcLine.createAssigns(createAssignEntityList);
            allAssignEntityList.addAll(createAssignEntityList);
        }
        // 审核通过或部分过账, 更新后需同步到EBS
        rtcLine = getLine(allAssignEntityList);
        if (Objects.nonNull(rtcLine)) {
            // 更新行数量
            this.updateLineQty(rtcLine);
            WipMtrRtcHeaderEntity rtcHeader = WipMtrRtcHeaderEntity.get().getById(rtcLine.getHeaderId());
            if (WipMtrRtcHeaderStatusEnum.effective(rtcHeader.getBillStatus())) {
                rtcHeader.setLineList(Collections.singletonList(rtcLine));
                wipMtrRtcWriteBackService.update(rtcHeader);
            }
        }
        return "";
    }

    private WipMtrRtcLineEntity getLine(List<WipMtrRtcAssignEntity> assignList) {
        if (ListUtil.empty(assignList)) {
            return null;
        }
        WipMtrRtcLineEntity rtcLine = WipMtrRtcLineEntity.get().getById(assignList.get(0).getLineId());
        rtcLine.setAssignList(assignList);
        return rtcLine;
    }

    private void updateLineQty(WipMtrRtcLineEntity rtcLine) {
        BigDecimal sumAssignQty = rtcLine.getAssignList().stream().map(WipMtrRtcAssignEntity::getAssignQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumIssuedQty = rtcLine.getAssignList().stream().map(WipMtrRtcAssignEntity::getIssuedQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!sumAssignQty.equals(rtcLine.getReqQty()) || !sumIssuedQty.equals(rtcLine.getIssuedQty())) {
            rtcLine.setReqQty(sumAssignQty)
                    .setIssuedQty(sumIssuedQty);
            rtcLine.update();
        }
    }

}
