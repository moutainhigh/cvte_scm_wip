package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.BatchProcessUtils;
import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrSubInvRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
    private WipMtrRtcLineRepository wipMtrRtcLineRepository;
    private WipMtrSubInvRepository wipMtrSubInvRepository;
    private WipMtrRtcLotControlService wipMtrRtcLotControlService;

    public WipMtrRtcViewService(ViewService viewService, WipMtrRtcLineRepository wipMtrRtcLineRepository, WipMtrSubInvRepository wipMtrSubInvRepository, WipMtrRtcLotControlService wipMtrRtcLotControlService) {
        this.viewService = viewService;
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
        List<String> itemIdList = data.stream().map(line -> (String) line.get("itemId")).collect(Collectors.toList());

        List<WipMtrSubInvVO> mtrSubInvVOList = getMtrSubInv(data);
        // 物料库存现有量
        Map<String, BigDecimal> mtrSupplyQtyMap = WipMtrSubInvVO.groupQtyByItem(mtrSubInvVOList);
        // 物料子库现有量
        Map<String, BigDecimal> mtrSubInvQtyMap = WipMtrSubInvVO.groupQtyByItemSub(mtrSubInvVOList);
        // 物料可用量
        List<WipReqItemVO> unPostReqItemVOList = getReqItem(data.get(0), itemIdList);
        Map<String, BigDecimal> unPostReqItemVOMap = WipReqItemVO.groupUnPostQtyByItemSub(unPostReqItemVOList);
        // 批次强管控物料
        List<String> rtcLotControlItemList = wipMtrRtcLotControlService.getLotControlItem(organizationId, itemIdList);

        for (Map<String, Object> line : data) {
            String itemId = (String)line.get("itemId");
            String invpNo = (String)line.get("invpNo");
            String subInvKey = BatchProcessUtils.getKey(itemId, invpNo);

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

    private List<WipReqItemVO> getReqItem(Map<String, Object> firstLine, List<String> itemIdList) {
        WipMtrRtcLineQueryVO wipMtrRtcLineQueryVO = WipMtrRtcLineQueryVO.buildForUnPost(
                (String) firstLine.get("organizationId"),
                (String) firstLine.get("factoryId"),
                (String) firstLine.get("headerId"),
                (String) firstLine.get("billType"),
                itemIdList
        );
        return wipMtrRtcLineRepository.batchSumUnPostQty(wipMtrRtcLineQueryVO);
    }

    public List<WipMtrSubInvVO> getMtrSubInv(List<Map<String, Object>> data) {
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
