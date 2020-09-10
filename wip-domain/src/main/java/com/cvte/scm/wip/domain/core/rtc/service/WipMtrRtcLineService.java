package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.scm.wip.domain.core.requirement.service.WipReqItemService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public WipMtrRtcLineService(WipReqItemService wipReqItemService, CheckMtrRtcLineService checkMtrRtcLineService) {
        this.wipReqItemService = wipReqItemService;
        this.checkMtrRtcLineService = checkMtrRtcLineService;
    }

    public String batchUpdate(List<WipMtrRtcLineBuildVO> rtcLineBuildVOList) {
        String[] lineIdList = rtcLineBuildVOList.stream().map(WipMtrRtcLineBuildVO::getLineId).toArray(String[]::new);
        // 查询单据行
        List<WipMtrRtcLineEntity> rtcLineEntityList = WipMtrRtcLineEntity.get().getByLineIds(lineIdList);
        Map<String, WipMtrRtcLineEntity> rtcLineEntityMap = rtcLineEntityList.stream().collect(Collectors.toMap(WipMtrRtcLineEntity::getUniqueId, Function.identity()));

        // 查询单据头
        WipMtrRtcHeaderEntity rtcHeaderEntity = getHeader(rtcLineBuildVOList);
        // 查询工单投料信息
        List<WipReqItemVO> reqItemVOList = getItemVOList(rtcHeaderEntity, rtcLineEntityList);
        Map<String, WipReqItemVO> reqItemVOMap = reqItemVOList.stream().collect(Collectors.toMap(WipReqItemVO::getKey, Function.identity()));

        for (WipMtrRtcLineBuildVO rtcLineBuildVO : rtcLineBuildVOList) {
            // 校验数量下限
            checkMtrRtcLineService.checkQtyLower(rtcLineBuildVO);
            // 取整
            checkMtrRtcLineService.roundQty(rtcLineBuildVO);

            WipMtrRtcLineEntity rtcLineEntity = rtcLineEntityMap.get(rtcLineBuildVO.getLineId());
            WipReqItemVO reqItemVO = reqItemVOMap.get(rtcLineEntity.getReqKey(rtcHeaderEntity.getMoId()));
            // 校验实发数量
            checkMtrRtcLineService.checkIssuedQty(rtcLineBuildVO, rtcLineEntity);
            // 限制数量
            if (Objects.nonNull(rtcLineBuildVO.getReqQty())) {
                checkMtrRtcLineService.checkQtyUpper(rtcLineBuildVO.getReqQty(), reqItemVO, rtcHeaderEntity.getBillType());
            }
            if (Objects.nonNull(rtcLineBuildVO.getIssuedQty())) {
                checkMtrRtcLineService.checkQtyUpper(rtcLineBuildVO.getIssuedQty(), reqItemVO, rtcHeaderEntity.getBillType());
            }
            // 更新单据行
            rtcLineEntity.update(rtcLineBuildVO);
        }
        WipMtrRtcHeaderEntity.get().setLineList(rtcLineEntityList).saveLines(false);
        return "";
    }

    private WipMtrRtcHeaderEntity getHeader(List<WipMtrRtcLineBuildVO> rtcLineModifyVOList) {
        String headerId = rtcLineModifyVOList.get(0).getHeaderId();
        return WipMtrRtcHeaderEntity.get().getById(headerId);
    }

    private List<WipReqItemVO> getItemVOList(WipMtrRtcHeaderEntity rtcHeaderEntity, List<WipMtrRtcLineEntity> rtcLineEntityList) {
        List<String> itemKeyList = rtcLineEntityList.stream().map(WipMtrRtcLineEntity::getItemId).collect(Collectors.toList());
        WipMtrRtcHeaderBuildVO rtcHeaderBuildVO = new WipMtrRtcHeaderBuildVO();
        rtcHeaderBuildVO.setOrganizationId(rtcHeaderEntity.getOrganizationId())
                .setMoId(rtcHeaderEntity.getMoId())
                .setHeaderId(rtcHeaderEntity.getHeaderId())
                .setBillType(rtcHeaderEntity.getBillType())
                .setWkpNo(rtcHeaderEntity.getWkpNo())
                .setItemList(itemKeyList);
        return wipReqItemService.getReqItemList(rtcHeaderBuildVO);
    }

}
