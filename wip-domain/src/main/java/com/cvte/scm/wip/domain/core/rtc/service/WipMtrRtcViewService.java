package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.BatchProcessUtils;
import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqItemService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrSubInvRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcQueryVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLotControlTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/17 17:15
 */
@Service
public class WipMtrRtcViewService {

    private ViewService viewService;
    private WipReqItemService wipReqItemService;
    private WipMtrRtcLineRepository wipMtrRtcLineRepository;
    private WipMtrSubInvRepository wipMtrSubInvRepository;
    private WipMtrRtcLotControlService wipMtrRtcLotControlService;

    public WipMtrRtcViewService(ViewService viewService, WipReqItemService wipReqItemService, WipMtrRtcLineRepository wipMtrRtcLineRepository, WipMtrSubInvRepository wipMtrSubInvRepository, WipMtrRtcLotControlService wipMtrRtcLotControlService) {
        this.viewService = viewService;
        this.wipReqItemService = wipReqItemService;
        this.wipMtrRtcLineRepository = wipMtrRtcLineRepository;
        this.wipMtrSubInvRepository = wipMtrSubInvRepository;
        this.wipMtrRtcLotControlService = wipMtrRtcLotControlService;
    }

    @SuppressWarnings("unchecked")
    public PageResultEntity lineView(SysViewPageParamVO sysViewPageParam) {
        PageResultEntity feignPageResult = viewService.getViewPageDataByViewPageParam(sysViewPageParam);
        List<Map<String, Object>> data = (List<Map<String, Object>>) feignPageResult.getList();
        if (ListUtil.empty(data)) {
            return feignPageResult;
        }

        String organizationId = (String) data.get(0).get("organizationId");
        String moId = (String) data.get(0).get("moId");
        List<String> itemIdList = data.stream().map(line -> (String) line.get("itemId")).collect(Collectors.toList());

        List<WipReqItemVO> reqItemList = getReqItem(organizationId, moId, itemIdList);
        Map<String, WipReqItemVO> reqItemMap = reqItemList.stream().collect(Collectors.toMap(WipReqItemVO::getKey, Function.identity()));

        List<WipMtrSubInvVO> mtrSubInvVOList = getMtrSubInv(data);
        // 物料库存现有量
        Map<String, BigDecimal> mtrSupplyQtyMap = WipMtrSubInvVO.groupQtyByItem(mtrSubInvVOList);
        // 物料子库现有量
        Map<String, BigDecimal> mtrSubInvQtyMap = WipMtrSubInvVO.groupQtyByItemSub(mtrSubInvVOList);
        // 物料已申请未过账数量
        List<WipReqItemVO> unPostReqItemVOList = getItemUnPost(data.get(0), itemIdList);
        Map<String, BigDecimal> unPostReqItemVOMap = WipReqItemVO.groupUnPostQtyByItemSub(unPostReqItemVOList);
        // 批次强管控物料
        List<String> rtcLotControlItemList = wipMtrRtcLotControlService.getLotControlItem(organizationId, itemIdList);

        for (Map<String, Object> line : data) {
            String itemId = (String)line.get("itemId");
            String invpNo = (String)line.get("invpNo");
            String subInvKey = BatchProcessUtils.getKey(itemId, invpNo);
            String itemKey = BatchProcessUtils.getKey(organizationId, moId, itemId, (String) line.get("wkpNo"));

            WipReqItemVO reqItem = reqItemMap.get(itemKey);
            if (Objects.nonNull(reqItem)) {
                line.put("itemQty", reqItem.getReqQty());
                line.put("itemIssuedQty", reqItem.getIssuedQty());
                line.put("itemUnissuedQty", reqItem.getUnIssuedQty());
            }

            BigDecimal supplyQty;
            if (StringUtils.isBlank(invpNo)) {
                // 子库为空, 获取库存现有量
                supplyQty = mtrSupplyQtyMap.get(itemId);
            } else {
                // 获取子库现有量
                supplyQty = mtrSubInvQtyMap.get(subInvKey);
            }
            if (Objects.nonNull(supplyQty)) {
                line.put("invQty", supplyQty);
                line.put("availQty", supplyQty);
            }

            BigDecimal unPostQty = unPostReqItemVOMap.get(subInvKey);
            if (Objects.nonNull(unPostQty) && Objects.nonNull(supplyQty)) {
                line.put("availQty", supplyQty.subtract(unPostQty));
            }

            if (rtcLotControlItemList.contains(itemId)) {
                line.put("lotControlStatus", "1");
            }
        }

        return feignPageResult;
    }

    /**
     *
     * @since 2020/11/5 7:09 下午
     * @author xueyuting
     * @param lotNumber 批次过滤参数
     */
    public List<WipMtrSubInvVO> lotView(WipMtrRtcQueryVO lotQuery, String lotNumber) {
        if (StringUtils.isBlank(lotQuery.getOrganizationId()) || StringUtils.isBlank(lotQuery.getFactoryId()) || StringUtils.isBlank(lotQuery.getItemId()) || StringUtils.isBlank(lotQuery.getMoId())) {
            throw new ParamsIncorrectException("参数不全");
        }
        List<WipMtrSubInvVO> mtrSubInvVOList = wipMtrRtcLotControlService.getItemLot(lotQuery.getOrganizationId(), lotQuery.getFactoryId(), lotQuery.getItemId(), lotQuery.getMoId(), lotQuery.getInvpNo());

        if (StringUtils.isNotBlank(lotNumber)) {
            mtrSubInvVOList.removeIf(vo -> !vo.getLotNumber().contains(lotNumber));
        }

        if (ListUtil.empty(mtrSubInvVOList)) {
            return Collections.emptyList();
        }
        boolean isWeakControl = WipMtrRtcLotControlTypeEnum.WEAK_CONTROL.getCode().equals(mtrSubInvVOList.get(0).getLotControlType());
        if (isWeakControl) {
            // 弱管控批次(可发任意库存批次), 补充物料需求&领料数量
            WipMtrRtcLineEntity rtcLine = WipMtrRtcLineEntity.get().getById(lotQuery.getLineId());
            BigDecimal rtcLineIssuedQty = rtcLine.getIssuedQty();
            for (WipMtrSubInvVO weakControlVO : mtrSubInvVOList) {
                if (BigDecimal.ZERO.compareTo(weakControlVO.getItemQty()) >= 0) {
                    // 弱管控且未配置投料批次时, 需求数量 = min(领料行实发数量, 库存现有量)
                    BigDecimal itemQty = rtcLineIssuedQty.min(Optional.ofNullable(weakControlVO.getSupplyQty()).orElse(BigDecimal.ZERO));
                    weakControlVO.setItemQty(itemQty);
                }
            }
        }

        List<String> mtrLotNoColl = mtrSubInvVOList.stream().map(WipMtrSubInvVO::getLotNumber).collect(Collectors.toList());
        lotQuery.setMtrLotNoColl(mtrLotNoColl)
                // 未过账状态
                .setStatusColl(WipMtrRtcLineStatusEnum.getUnPostStatus());
        // 获取物料批次已申请未过账数量
        List<WipReqItemVO> lotUnPostList = wipMtrRtcLineRepository.batchSumItemLotUnPostQty(lotQuery);
        Map<String, WipReqItemVO> lotUnPostMap = lotUnPostList.stream().collect(Collectors.toMap(item -> item.getInvpNo() + item.getLotNumber(), Function.identity()));
        for (WipMtrSubInvVO mtrSubInvVO : mtrSubInvVOList) {
            // 计算可用量
            BigDecimal availQty = mtrSubInvVO.getSupplyQty();
            WipReqItemVO lotUnPost = lotUnPostMap.get(mtrSubInvVO.getSubinventoryCode() + mtrSubInvVO.getLotNumber());
            if (Objects.nonNull(lotUnPost)) {
                availQty = availQty.subtract(Optional.ofNullable(lotUnPost.getUnPostQty()).orElse(BigDecimal.ZERO));
            }
            mtrSubInvVO.setAvailQty(availQty);
        }

        return mtrSubInvVOList;
    }

    private List<WipReqItemVO> getReqItem(String organizationId, String moId, List<String> itemIdList) {
        WipReqLineKeyQueryVO reqLineQueryVO = WipReqLineKeyQueryVO.buildForReqItem(organizationId, moId, null, itemIdList);
        return wipReqItemService.getReqItem(reqLineQueryVO);
    }

    private List<WipReqItemVO> getItemUnPost(Map<String, Object> firstLine, List<String> itemIdList) {
        WipMtrRtcQueryVO wipMtrRtcQueryVO = WipMtrRtcQueryVO.buildForItemUnPost(
                (String) firstLine.get("organizationId"),
                (String) firstLine.get("factoryId"),
                (String) firstLine.get("headerId"),
                (String) firstLine.get("billType"),
                itemIdList
        );
        return wipMtrRtcLineRepository.batchSumItemUnPostQty(wipMtrRtcQueryVO);
    }

    private List<WipMtrSubInvVO> getMtrSubInv(List<Map<String, Object>> data) {
        List<WipMtrSubInvVO> queryVOList = new ArrayList<>();
        for (Map<String, Object> line : data) {
            WipMtrSubInvVO queryVO = new WipMtrSubInvVO();
            queryVO.setOrganizationId((String) line.get("organizationId"))
                    .setFactoryId((String) line.get("factoryId"))
                    .setInventoryItemId((String) line.get("itemId"))
                    .setSubinventoryCode((String) line.get("invpNo"));
            queryVOList.add(queryVO);
        }
        return wipMtrSubInvRepository.selectByVO(queryVOList);
    }

}
