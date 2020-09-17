package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.item.entity.ScmItemEntity;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqHeaderService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrSubInvRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLotControlVO;
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

    private static final String OPTION_NO = "PUR_RULE_01";
    private static final String OPTION_VALUE = "1";

    private ViewService viewService;
    private WipReqHeaderService wipReqHeaderService;
    private ScmItemService scmItemService;
    private WipMtrRtcLineRepository wipMtrRtcLineRepository;
    private WipMtrSubInvRepository wipMtrSubInvRepository;
    private WipMtrRtcLotControlService wipMtrRtcLotControlService;

    public WipMtrRtcViewService(ViewService viewService, WipReqHeaderService wipReqHeaderService, ScmItemService scmItemService, WipMtrRtcLineRepository wipMtrRtcLineRepository, WipMtrSubInvRepository wipMtrSubInvRepository, WipMtrRtcLotControlService wipMtrRtcLotControlService) {
        this.viewService = viewService;
        this.wipReqHeaderService = wipReqHeaderService;
        this.scmItemService = scmItemService;
        this.wipMtrRtcLineRepository = wipMtrRtcLineRepository;
        this.wipMtrSubInvRepository = wipMtrSubInvRepository;
        this.wipMtrRtcLotControlService = wipMtrRtcLotControlService;
    }

    @SuppressWarnings("unchecked")
    public PageResultEntity lineView(SysViewPageParamVO sysViewPageParam) {
        PageResultEntity feignPageResult = viewService.getViewPageDataByViewPageParam(sysViewPageParam);
        List<Map<String, Object>> data = (List<Map<String, Object>>) feignPageResult.getList();

        String organizationId = (String) data.get(0).get("organizationId");
        String moId = (String) data.get(0).get("moId");
        List<String> itemIdList = data.stream().map(line -> (String) line.get("itemId")).collect(Collectors.toList());

        List<WipMtrSubInvVO> mtrSubInvVOList = getMtrSubInv(data);
        // 物料子库现有量
        Map<String, BigDecimal> mtrSubInvQtyMap = mtrSubInvVOList.stream().collect(Collectors.toMap(item -> getKey(item.getInventoryItemId(), item.getSubinventoryCode()), WipMtrSubInvVO::getSupplyQty));
        // 物料库存现有量
        Map<String, BigDecimal> mtrSupplyQtyMap = mtrSubInvVOList.stream().collect(Collectors.toMap(WipMtrSubInvVO::getInventoryItemId, WipMtrSubInvVO::getSupplyQty, BigDecimal::add));
        // 物料可用量
        List<WipReqItemVO> unPostReqItemVOList = getReqItem(data.get(0), itemIdList);
        Map<String, BigDecimal> unPostReqItemVOMap = unPostReqItemVOList.stream().collect(Collectors.toMap(item -> getKey(item.getItemId(), item.getInvpNo()), WipReqItemVO::getUnPostQty));
        // 批次强管控物料
        List<String> rtcLotControlItemList = getLotControlItem(organizationId, moId, itemIdList);

        for (Map<String, Object> line : data) {
            String itemId = (String)line.get("itemId");
            String invpNo = (String)line.get("invpNo");
            String subInvKey = getKey(itemId, invpNo);

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

    public List<String> getLotControlItem(String organizationId, String moId, List<String> itemIdList) {
        String productMinClass = getProductMinClass(organizationId, moId);
        List<WipMtrRtcLotControlVO> lotControlVOList = wipMtrRtcLotControlService.getLotControlByOptionNo(OPTION_NO, OPTION_VALUE);
        List<WipMtrRtcLotControlVO> filteredControlList = lotControlVOList.stream().filter(vo -> productMinClass.equals(vo.getProductClass())).collect(Collectors.toList());
        if (ListUtil.empty(filteredControlList)) {
            return Collections.emptyList();
        }
        List<String> controlItemMinClassList = filteredControlList.stream().map(WipMtrRtcLotControlVO::getMtrClass).collect(Collectors.toList());

        List<ScmItemEntity> itemEntityList = scmItemService.getByItemIds(organizationId, itemIdList);

        return itemEntityList.stream()
                .filter(item -> controlItemMinClassList.contains(item.getRdMinClassCode()))
                .map(ScmItemEntity::getItemId)
                .collect(Collectors.toList());
    }

    private String getProductMinClass(String organizationId, String moId) {
        WipReqHeaderEntity reqHeaderEntity = wipReqHeaderService.getBySourceId(moId);
        ScmItemEntity scmItemEntity = scmItemService.getByItemIds(organizationId, Collections.singletonList(reqHeaderEntity.getProductId())).get(0);
        return scmItemEntity.getRdMinClassCode();
    }

    private String getKey(String... keys) {
        List<String> keyList = new ArrayList<>();
        for (String key : keys) {
            if (StringUtils.isBlank(key)) {
                keyList.add("null");
                continue;
            }
            keyList.add(key);
        }
        return String.join("_", keyList);
    }

}
