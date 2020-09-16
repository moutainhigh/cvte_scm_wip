package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcAssignBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
                WipMtrRtcAssignEntity rtcAssignEntity = WipMtrRtcAssignEntity.get();
                rtcAssignEntity.create(rtcAssignBuildVO);
                createAssignEntityList.add(rtcAssignEntity);

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

        WipMtrRtcLineEntity rtcLineEntity = WipMtrRtcLineEntity.get();
        if (ListUtil.notEmpty(updateLineIdList)) {
            // 更新实体
            List<WipMtrRtcAssignEntity> updateAssignEntityList = new ArrayList<>();
            List<WipMtrRtcAssignEntity> rtcAssignEntityList = WipMtrRtcAssignEntity.get().getByIds(updateLineIdList.toArray(new String[0]));
            Map<String, WipMtrRtcAssignEntity> rtcAssignEntityMap = rtcAssignEntityList.stream().collect(Collectors.toMap(WipMtrRtcAssignEntity::getUniqueId, Function.identity()));
            for (WipMtrRtcAssignBuildVO rtcAssignBuildVO : rtcAssignBuildVOList) {
                WipMtrRtcAssignEntity rtcAssignEntity = rtcAssignEntityMap.get(rtcAssignBuildVO.getAssignId());
                rtcAssignEntity.update(rtcAssignBuildVO);
                updateAssignEntityList.add(rtcAssignEntity);
            }
            // 批量更新
            if (ListUtil.notEmpty(updateAssignEntityList)) {
                rtcLineEntity.updateAssigns(updateAssignEntityList);
            }
        }
        if (ListUtil.notEmpty(deleteAssignEntityList)) {
            // 批量删除
            rtcLineEntity.deleteAssigns(deleteAssignEntityList);
        }
        if (ListUtil.notEmpty(createAssignEntityList)) {
            // 批量创建
            rtcLineEntity.createAssigns(createAssignEntityList);
        }
        return "";
    }

}
