package com.cvte.scm.wip.controller.ckd.job;

import com.alibaba.fastjson.JSONObject;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.constants.CommonUserConstant;
import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskDeliveringStockView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockLineEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcWfEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskDeliveryStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.service.*;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.EbsInoutStockView;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.query.EbsInoutStockQuery;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.enums.EbsDeliveryStatusEnum;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.service.EbsInvokeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zy
 * @date 2020-05-12 11:45
 **/
@Slf4j
@Component
@JobHander("mcTaskInoutStockJob")
public class McTaskInoutStockJob extends IJobHandler {

    @Autowired
    private EbsInvokeService ebsInvokeService;

    @Autowired
    private WipMcTaskService wipMcTaskService;

    @Autowired
    private WipMcTaskVersionService wipMcTaskVersionService;

    @Autowired
    private WipMcWfService wipMcWfService;

    @Autowired
    private WipMcInoutStockService wipMcInoutStockService;

    @Autowired
    private WipMcInoutStockLineService wipMcInoutStockLineService;

    @Override
    public ReturnT<String> execute(Map<String, Object> map) {

        CurrentContext.setCurrentOperatingUser(CurrentContextUtils.mockOperatingUser(CommonUserConstant.TIMING_TASK));

        // 回写调拨出库数据
        writeBackInoutStock(new WriteBackHook() {

            @Override
            public List<McTaskDeliveringStockView> listMcTaskDeliveringStockView() {
                return wipMcTaskService.listMcTaskDeliveringOutStockView();
            }

            @Override
            public boolean needUpdateStatusToFinish() {
                return true;
            }
        });


        // 回写调拨入库数据
        writeBackInoutStock(new WriteBackHook() {

            @Override
            public List<McTaskDeliveringStockView> listMcTaskDeliveringStockView() {
                return wipMcTaskService.listMcTaskDeliveringInStockView();
            }

            @Override
            public boolean needUpdateStatusToFinish() {
                return false;
            }
        });


        return new ReturnT<>("调拨单数据回写已完成");
    }


    @Transactional(transactionManager = "pgTransactionManager")
    public void writeBackInoutStock(WriteBackHook writeBackHook) {

        // 拉取需要回写的配料任务
        List<McTaskDeliveringStockView> deliveringStockViewList = writeBackHook.listMcTaskDeliveringStockView();
        if (CollectionUtils.isEmpty(deliveringStockViewList)) {
            return;
        }

        // 查询配料任务调拨数据
        List<EbsInoutStockView> ebsInoutStockViews = new ArrayList<>();

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
        // 回写调拨数据
        List<WipMcTaskEntity> wipMcTaskUpdateList = new ArrayList<>();
        List<WipMcInoutStockEntity> wipMcInoutStocks = new ArrayList<>();
        List<WipMcInoutStockLineEntity> wipMcInoutStockLines = new ArrayList<>();
        // 需要更新配料任务状态的
        Set<String> repWipMcTaskIds = new HashSet<>();
        for (EbsInoutStockView ebsInoutStockView : ebsInoutStockViews) {
            if (StringUtils.isBlank(ebsInoutStockView.getOrigSysSourceId())) {
                log.info("[McTaskInoutStockJob]: 获取行id错误 {}", JSONObject.toJSONString(ebsInoutStockView));
                continue;
            }

            // 更新调拨头
            WipMcInoutStockEntity wipMcInoutStock = new WipMcInoutStockEntity();
            wipMcInoutStock.setInoutStockId(deliveryIdAndInoutStockId.get(ebsInoutStockView.getTicketNo()))
                    .setStatus(ebsInoutStockView.getStatus());
            EntityUtils.writeCurUserStdUpdInfoToEntity(wipMcInoutStock);
            wipMcInoutStocks.add(wipMcInoutStock);

            // 更新调拨行
            WipMcInoutStockLineEntity wipMcInoutStockLine = new WipMcInoutStockLineEntity();
            wipMcInoutStockLine.setInoutStockLineId(ebsInoutStockView.getOrigSysSourceId())
                    .setQty(ebsInoutStockView.getPlanQty())
                    .setPostQty(ebsInoutStockView.getActualQty())
                    .setStatus(getInoutStockLineStatus(ebsInoutStockView.getPostedFlag(), ebsInoutStockView.getCancelledFlag()));
            EntityUtils.writeCurUserStdUpdInfoToEntity(wipMcInoutStockLine);
            wipMcInoutStockLines.add(wipMcInoutStockLine);


            String mcTaskId = inoutLineIdAndTaskIdMap.get(ebsInoutStockView.getOrigSysSourceId());

            if (needUpdateToFinishTaskIds.contains(mcTaskId)
                && (!EbsDeliveryStatusEnum.POSTED.getCode().equals(ebsInoutStockView.getStatus())
                    || (BooleanEnum.NO.getCode().equals(ebsInoutStockView.getCancelledFlag())
                             && !BooleanEnum.YES.getCode().equals(ebsInoutStockView.getPostedFlag()))
                    )) {
                // 头状态不是【已过账】，或行有【未取消】的【未过账数据】数据，则移除, 最后剩下的即是全部数据都已过账的
                needUpdateToFinishTaskIds.remove(mcTaskId);
            }

            if (EbsDeliveryStatusEnum.CANCELLED.getCode().equals(ebsInoutStockView.getStatus())) {
                // 已取消的调拨单 需要将配料任务状态更新为废弃
                String wfId = UUIDUtils.getUUID();

                WipMcWfEntity wipMcWf = new WipMcWfEntity();
                wipMcWf.setWfId(wfId)
                        .setMcTaskId(mcTaskId)
                        .setStatus(McTaskStatusEnum.CLOSE.getCode())
                        .setCrtTime(new Date())
                        .setCrtUser(CommonUserConstant.TIMING_TASK);
                wipMcWfService.insertSelective(wipMcWf);

                WipMcTaskEntity wipMcTask = new WipMcTaskEntity();
                wipMcTask.setMcTaskId(mcTaskId).setMcWfId(wfId);
                EntityUtils.writeCurUserStdUpdInfoToEntity(wipMcTask);
                wipMcTaskUpdateList.add(wipMcTask);
            }

            if (StringUtils.isBlank(mcTaskId) || repWipMcTaskIds.contains(mcTaskId)) {
                continue;
            }

            repWipMcTaskIds.add(mcTaskId);
        }


        // 需要更新为已完成的数据
        if (writeBackHook.needUpdateStatusToFinish()) {
            for (String updateToFinishId : needUpdateToFinishTaskIds) {

                // 预防ebs查询接口未返回配料任务数据，而导致误判调拨完成
                if (!repWipMcTaskIds.contains(updateToFinishId)) {
                    continue;
                }

                WipMcTaskEntity wipMcTask = new WipMcTaskEntity();
                wipMcTask.setMcTaskId(updateToFinishId)
                        .setMcWfId(addWf(updateToFinishId, McTaskStatusEnum.FINISH));
                EntityUtils.writeCurUserStdUpdInfoToEntity(wipMcTask);
                wipMcTaskUpdateList.add(wipMcTask);
            }
        }

        wipMcInoutStockService.updateList(wipMcInoutStocks);
        wipMcInoutStockLineService.updateList(wipMcInoutStockLines);
        wipMcTaskService.updateList(wipMcTaskUpdateList);


        // 同步版本信息
        for (String taskId : repWipMcTaskIds) {
            wipMcTaskVersionService.sync(taskId, false);
        }
    }

    private String getInoutStockLineStatus(String postedFlag, String cancelledFlag) {

        if (BooleanEnum.YES.getCode().equals(cancelledFlag)) {
            return McTaskDeliveryStatusEnum.CANCELLED.getCode();
        } else if (BooleanEnum.YES.getCode().equals(postedFlag)) {
            return McTaskDeliveryStatusEnum.POSTED.getCode();
        }
        return McTaskDeliveryStatusEnum.UN_POST.getCode();
    }


    private String addWf(String taskId, McTaskStatusEnum mcTaskStatusEnum) {

        WipMcWfEntity wipMcWf = new WipMcWfEntity();
        wipMcWf.setWfId(UUIDUtils.getUUID())
                .setMcTaskId(taskId)
                .setStatus(mcTaskStatusEnum.getCode())
                .setCrtTime(new Date())
                .setCrtUser(CommonUserConstant.TIMING_TASK);
        wipMcWfService.insertSelective(wipMcWf);
        return wipMcWf.getWfId();
    }
    private String addWf(String taskId, EbsInoutStockView ebsInoutStockView) {
        String wfId = null;
        if (EbsDeliveryStatusEnum.CANCELLED.getCode().equals(ebsInoutStockView.getStatus())) {
            wfId = UUIDUtils.getUUID();

            WipMcWfEntity wipMcWf = new WipMcWfEntity();
            wipMcWf.setWfId(wfId)
                    .setMcTaskId(taskId)
                    .setStatus(McTaskStatusEnum.CLOSE.getCode())
                    .setCrtTime(new Date())
                    .setCrtUser(CommonUserConstant.TIMING_TASK);
            wipMcWfService.insertSelective(wipMcWf);
        }
        return wfId;
    }

    interface WriteBackHook {

        /**
         * 获取正在进行中的调拨数据
         *
         * @return java.util.List<com.cvte.scm.wip.ckd.dto.view.McTaskDeliveringStockView>
         **/
        List<McTaskDeliveringStockView> listMcTaskDeliveringStockView();

        boolean needUpdateStatusToFinish();
    }

}
