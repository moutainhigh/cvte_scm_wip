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
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskDeliveringStockView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockLineEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcWfEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskDeliveryStatusEnum;
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
                .collect(Collectors.toMap(McTaskDeliveringStockView::getInoutStockLineId, McTaskDeliveringStockView::getMcTaskId));
        Map<String, String> deliveryIdAndInoutStockId = new HashMap<>();
        for (McTaskDeliveringStockView mcTaskDeliveringStockView : deliveringStockViewList) {
            if (deliveryIdAndInoutStockId.containsKey(mcTaskDeliveringStockView.getDeliveryNo())) {
                continue;
            }
            deliveryIdAndInoutStockId.put(mcTaskDeliveringStockView.getDeliveryNo(), mcTaskDeliveringStockView.getInoutStockId());
        }

        Set<String> needUpdateToFinishTaskIds = deliveringStockViewList.stream()
                .map(McTaskDeliveringStockView::getMcTaskId).collect(Collectors.toSet());
        Set<String> needUpdateToCloseTaskIds = new HashSet<>(needUpdateToFinishTaskIds);
        // 回写调拨数据
        List<WipMcTaskEntity> wipMcTaskUpdateList = new ArrayList<>();
        List<WipMcInoutStockEntity> wipMcInoutStocks = new ArrayList<>();
        List<WipMcInoutStockLineEntity> wipMcInoutStockLines = new ArrayList<>();
        // 需要更新配料任务状态的
        Set<String> repWipMcTaskIds = new HashSet<>();
        for (EbsInoutStockVO ebsInoutStockView : ebsInoutStockViews) {
            if (StringUtils.isBlank(ebsInoutStockView.getOrigSysSourceId())) {
                log.info("[McTaskInoutStockJob]: 获取行id错误 {}", JSONObject.toJSONString(ebsInoutStockView));
                continue;
            }

            // 更新调拨头
            String mcTaskId = inoutLineIdAndTaskIdMap.get(ebsInoutStockView.getOrigSysSourceId());
            if (!repWipMcTaskIds.contains(mcTaskId)) {
                WipMcInoutStockEntity wipMcInoutStock = new WipMcInoutStockEntity();
                wipMcInoutStock.setInoutStockId(deliveryIdAndInoutStockId.get(ebsInoutStockView.getTicketNo()))
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



            if (needUpdateToFinishTaskIds.contains(mcTaskId)
                    && (!EbsDeliveryStatusEnum.POSTED.getCode().equals(ebsInoutStockView.getStatus())
                    || (BooleanEnum.NO.getCode().equals(ebsInoutStockView.getCancelledFlag())
                    && !BooleanEnum.YES.getCode().equals(ebsInoutStockView.getPostedFlag()))
            )) {
                // 头状态不是【已过账】，或行有【未取消】的【未过账数据】数据，则移除, 最后剩下的即是全部数据都已过账的
                needUpdateToFinishTaskIds.remove(mcTaskId);
            }


            if (!EbsDeliveryStatusEnum.CANCELLED.getCode().equals(ebsInoutStockView.getStatus())) {
                // 含有调拨头状态不是取消的，则移除，最后剩下的即是需要进去关闭状态的
                needUpdateToCloseTaskIds.remove(mcTaskId);
            }

            if (StringUtils.isBlank(mcTaskId) || repWipMcTaskIds.contains(mcTaskId)) {
                continue;
            }

            repWipMcTaskIds.add(mcTaskId);
        }


        if (writeBackHook.needUpdateStatusToFinish()) {
            wipMcTaskUpdateList.addAll(handlerNeedUpdateStatusTasks(needUpdateToFinishTaskIds, McTaskStatusEnum.FINISH));
        }
        wipMcTaskUpdateList.addAll(handlerNeedUpdateStatusTasks(needUpdateToCloseTaskIds, McTaskStatusEnum.CLOSE));


        wipMcInoutStockService.updateList(wipMcInoutStocks);
        wipMcInoutStockLineService.updateList(wipMcInoutStockLines);
        wipMcTaskService.updateList(wipMcTaskUpdateList);


        // 同步版本信息
        for (String taskId : repWipMcTaskIds) {
            wipMcTaskVersionService.sync(taskId, false);
        }
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
