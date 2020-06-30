package com.cvte.scm.wip.domain.core.ckd.service;

import com.alibaba.fastjson.JSONObject;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.constants.CommonUserConstant;
import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.log.constant.LogModuleConstant;
import com.cvte.scm.wip.domain.common.log.dto.WipLogDTO;
import com.cvte.scm.wip.domain.common.log.service.WipOperationLogService;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskDeliveringStockView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockLineEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcWfEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskDeliveryStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskFinishStatusEnums;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskLineStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.hook.WriteBackHook;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockVO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.query.EbsInoutStockQuery;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.enums.EbsDeliveryStatusEnum;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.service.EbsInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zy
 * @date 2020-05-28 19:27
 **/
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMcInoutStockWriteBackService {


    @Autowired
    private EbsInvokeService ebsInvokeService;

    @Autowired
    private WipMcTaskVersionService wipMcTaskVersionService;

    @Autowired
    private WipMcWfService wipMcWfService;

    @Autowired
    private WipMcInoutStockService wipMcInoutStockService;

    @Autowired
    private WipMcInoutStockLineService wipMcInoutStockLineService;

    @Autowired
    private WipOperationLogService wipOperationLogService;

    @Autowired
    private WipMcTaskService wipMcTaskService;

    @Autowired
    private WipMcTaskLineService wipMcTaskLineService;


    public void writeBackInoutStock(WriteBackHook writeBackHook) {

        // 拉取需要回写的配料任务
        List<McTaskDeliveringStockView> deliveringStockViewList = writeBackHook.listMcTaskDeliveringStockView();
        if (CollectionUtils.isEmpty(deliveringStockViewList)) {
            return;
        }

        // 查询配料任务调拨数据
        List<EbsInoutStockVO> ebsInoutStockViews = new ArrayList<>();

        Set<String> outStockNoSet = deliveringStockViewList
                .stream().map(McTaskDeliveringStockView::getDeliveryNo).collect(Collectors.toSet());
        List<List<String>> partitionLists = ListUtils.partition(new ArrayList<>(outStockNoSet), 200);
        for (List<String> partitionList : partitionLists) {
            ebsInoutStockViews.addAll(
                    ebsInvokeService.listEbsInoutStockView(new EbsInoutStockQuery().setTicketNoList(partitionList)));
        }

        Map<String, String> inoutLineIdAndTaskIdMap = deliveringStockViewList.stream()
                .filter(el -> StringUtils.isNotBlank(el.getInoutStockLineId()))
                .collect(Collectors.toMap(McTaskDeliveringStockView::getInoutStockLineId, McTaskDeliveringStockView::getMcTaskId));
        Map<String, String> deliveryNoAndInoutStockId = new HashMap<>();
        for (McTaskDeliveringStockView mcTaskDeliveringStockView : deliveringStockViewList) {
            if (deliveryNoAndInoutStockId.containsKey(mcTaskDeliveringStockView.getDeliveryNo())) {
                continue;
            }
            deliveryNoAndInoutStockId.put(mcTaskDeliveringStockView.getDeliveryNo(), mcTaskDeliveringStockView.getInoutStockId());
        }

        // 回写调拨数据
        List<WipMcTaskEntity> wipMcTaskUpdateList = new ArrayList<>();
        List<WipMcInoutStockEntity> wipMcInoutStocks = new ArrayList<>();
        List<WipMcInoutStockLineEntity> wipMcInoutStockLines = new ArrayList<>();
        // 辅助性字段，记录ebs返回调拨数据相关的mcTaskIds
        Set<String> repWipMcTaskIds = new HashSet<>();
        for (EbsInoutStockVO ebsInoutStockView : ebsInoutStockViews) {
            if (StringUtils.isBlank(ebsInoutStockView.getOrigSysSourceId())) {
                log.info("[McTaskInoutStockJob]: 获取行id错误 {}", JSONObject.toJSONString(ebsInoutStockView));
                continue;
            }

            // 更新调拨头
            String mcTaskId = inoutLineIdAndTaskIdMap.get(ebsInoutStockView.getOrigSysSourceId());
            if (StringUtils.isBlank(mcTaskId)){
                continue;
            }

            if (!repWipMcTaskIds.contains(mcTaskId)) {
                WipMcInoutStockEntity wipMcInoutStock = new WipMcInoutStockEntity();
                wipMcInoutStock.setInoutStockId(deliveryNoAndInoutStockId.get(ebsInoutStockView.getTicketNo()))
                        .setStatus(ebsInoutStockView.getStatus());
                EntityUtils.writeCurUserStdUpdInfoToEntity(wipMcInoutStock);
                wipMcInoutStocks.add(wipMcInoutStock);
            }

            // 更新调拨行
            WipMcInoutStockLineEntity wipMcInoutStockLine = new WipMcInoutStockLineEntity();
            wipMcInoutStockLine.setInoutStockLineId(ebsInoutStockView.getOrigSysSourceId())
                    .setQty(ebsInoutStockView.getPlanQty())
                    .setPostQty(ebsInoutStockView.getActualQty())
                    .setStatus(getInoutStockLineStatus(ebsInoutStockView.getStatus(), ebsInoutStockView.getPostedFlag(), ebsInoutStockView.getCancelledFlag()));
            EntityUtils.writeCurUserStdUpdInfoToEntity(wipMcInoutStockLine);
            wipMcInoutStockLines.add(wipMcInoutStockLine);

            repWipMcTaskIds.add(mcTaskId);
        }

        // 更新调拨数据
        wipMcInoutStockService.updateList(wipMcInoutStocks);
        wipMcInoutStockLineService.updateList(wipMcInoutStockLines);



        // 拉取所有更新了的配料任务行，并筛选出需要更新状态的配料任务id
        List<WipMcTaskLineView> wipMcTaskLineViews = wipMcTaskLineService.listWipMcTaskLineView(
                new WipMcTaskLineQuery().setTaskIds(new ArrayList<>(repWipMcTaskIds)).setLineStatus(McTaskLineStatusEnum.NORMAL.getCode()));

        // 更新配料任务完成状态
        if (writeBackHook.needUpdateFinishStatusToFinish()) {
            updateMcTaskFinishStatusToFinish(needUpdateMcTaskFinishStatusToFinish(wipMcTaskLineViews));
        }


        Set<String> needUpdateToFinishTaskIds = findNeedUpdateStatusToFinishTaskIds(repWipMcTaskIds, wipMcTaskLineViews);
        Set<String> needUpdateToCloseTaskIds = findNeedUpdateStatusToCancelTaskIds(repWipMcTaskIds, wipMcTaskLineViews);

        // 避免ebs无数据返回而导致误更新
        wipMcTaskUpdateList.addAll(handlerNeedUpdateStatusTasks(needUpdateToFinishTaskIds, McTaskStatusEnum.FINISH));
        wipMcTaskUpdateList.addAll(handlerNeedUpdateStatusTasks(needUpdateToCloseTaskIds, McTaskStatusEnum.CLOSE));

        wipMcTaskService.updateList(wipMcTaskUpdateList);


        // 同步版本信息
        for (String taskId : repWipMcTaskIds) {
            wipMcTaskVersionService.sync(taskId, false);
        }
    }

    /**
     * 更新配料任务完成状态至完成
     *
     * @param mcTaskIds
     * @return void
     **/
    private void updateMcTaskFinishStatusToFinish(List<String> mcTaskIds) {
        if (CollectionUtils.isEmpty(mcTaskIds)) {
            return;
        }

        List<WipMcTaskEntity> updateList = new ArrayList<>();
        for (String mcTaskId : mcTaskIds) {
            WipMcTaskEntity wipMcTaskEntity = new WipMcTaskEntity();
            wipMcTaskEntity.setMcTaskId(mcTaskId)
                    .setFinishStatus(McTaskFinishStatusEnums.FINISH.getCode())
                    .setFinishDate(new Date());
            EntityUtils.writeCurUserStdUpdInfoToEntity(wipMcTaskEntity);
            updateList.add(wipMcTaskEntity);
        }
        wipMcTaskService.updateList(updateList);
    }

    /**
     * 所有调拨出库完成时，更新配料任务头状态为完成
     *
     * @param wipMcTaskLineViews
     * @return java.util.List<java.lang.String>
     **/
    private List<String> needUpdateMcTaskFinishStatusToFinish(List<WipMcTaskLineView> wipMcTaskLineViews) {
        Set<String> allIds = wipMcTaskLineViews.stream().map(WipMcTaskLineView::getMcTaskId).collect(Collectors.toSet());

        for (WipMcTaskLineView view : wipMcTaskLineViews) {
            if (!McTaskDeliveryStatusEnum.POSTED.getCode().equals(view.getDeliveryInLineStatus())
                || !EbsDeliveryStatusEnum.POSTED.getCode().equals(view.getDeliveryInStatus())) {
                allIds.remove(view.getMcTaskId());
            }
        }
        return new ArrayList<>(allIds);
    }
    /**
     * 当前业务场景下：
     *  1. 调拨出库过账后才能调拨入库
     *  2. 调拨出库全部过账后状态更新为完成，全部为取消后更新为关闭。
     *  综上，判断是否更新配料任务是否更新时仅判断出库状态即可
     *
     * @param allMcTaskIds
     * @param wipMcTaskLineViews
     * @return java.util.Set<java.lang.String>
     **/
    private Set<String> findNeedUpdateStatusToCancelTaskIds(Set<String> allMcTaskIds,
                                                    List<WipMcTaskLineView> wipMcTaskLineViews) {
        Set<String> filterIds = wipMcTaskLineViews.stream()
                .filter(el -> !McTaskDeliveryStatusEnum.CANCELLED.getCode().equals(el.getDeliveryOutLineStatus()))
                .map(WipMcTaskLineView::getMcTaskId)
                .collect(Collectors.toSet());
        Set<String> afterFilter = new HashSet<>(allMcTaskIds);
        afterFilter.removeAll(filterIds);
        afterFilter.retainAll(wipMcTaskLineViews.stream().map(WipMcTaskLineView::getMcTaskId).collect(Collectors.toSet()));
        return afterFilter;
    }


    private Set<String> findNeedUpdateStatusToFinishTaskIds(Set<String> allMcTaskIds,
                                                            List<WipMcTaskLineView> wipMcTaskLineViews) {
        Set<String> filterIds = wipMcTaskLineViews.stream()
                .filter(el -> !McTaskDeliveryStatusEnum.POSTED.getCode().equals(el.getDeliveryOutLineStatus())
                        || !EbsDeliveryStatusEnum.POSTED.getCode().equals(el.getDeliveryOutStatus()))
                .map(WipMcTaskLineView::getMcTaskId)
                .collect(Collectors.toSet());
        Set<String> afterFilter = new HashSet<>(allMcTaskIds);
        afterFilter.removeAll(filterIds);
        afterFilter.retainAll(wipMcTaskLineViews.stream().map(WipMcTaskLineView::getMcTaskId).collect(Collectors.toSet()));
        return afterFilter;
    }

    private List<WipMcTaskEntity> handlerNeedUpdateStatusTasks(Set<String> taskIds, McTaskStatusEnum mcTaskStatusEnum) {

        if (CollectionUtils.isEmpty(taskIds)) {
            return new ArrayList<>();
        }

        // 需要状态的
        List<WipMcTaskEntity> wipMcTaskUpdateList = new ArrayList<>();

        for (String taskId : taskIds) {
            String wfId = addWfIfCurNotConfirm(taskId, mcTaskStatusEnum);
            if (StringUtils.isBlank(wfId)) {
                continue;
            }

            WipMcTaskEntity wipMcTask = new WipMcTaskEntity();
            wipMcTask.setMcTaskId(taskId).setMcWfId(wfId);
            EntityUtils.writeCurUserStdUpdInfoToEntity(wipMcTask);
            wipMcTaskUpdateList.add(wipMcTask);
        }
        return wipMcTaskUpdateList;
    }

    private String getInoutStockLineStatus(String status, String postedFlag, String cancelledFlag) {

        if (EbsDeliveryStatusEnum.CANCELLED.getCode().equals(status)
                || BooleanEnum.YES.getCode().equals(cancelledFlag)) {
            return McTaskDeliveryStatusEnum.CANCELLED.getCode();
        } else if (BooleanEnum.YES.getCode().equals(postedFlag)) {
            return McTaskDeliveryStatusEnum.POSTED.getCode();
        }
        return McTaskDeliveryStatusEnum.UN_POST.getCode();
    }


    private String addWfIfCurNotConfirm(String taskId, McTaskStatusEnum mcTaskStatusEnum) {
        McTaskInfoView mcTaskInfoView = wipMcTaskService.getMcTaskInfoView(taskId);
        if (!McTaskStatusEnum.CONFIRM.getCode().equals(mcTaskInfoView.getStatus())) {
            return null;
        }

        WipMcWfEntity wipMcWf = new WipMcWfEntity();
        wipMcWf.setWfId(UUIDUtils.getUUID())
                .setMcTaskId(taskId)
                .setStatus(mcTaskStatusEnum.getCode())
                .setCrtTime(new Date())
                .setCrtUser(CommonUserConstant.TIMING_TASK);
        wipMcWfService.insertSelective(wipMcWf);

        wipOperationLogService.addLog(new WipLogDTO(LogModuleConstant.CKD,
                taskId,
                mcTaskStatusEnum.getOptName()));
        return wipMcWf.getWfId();
    }
}
