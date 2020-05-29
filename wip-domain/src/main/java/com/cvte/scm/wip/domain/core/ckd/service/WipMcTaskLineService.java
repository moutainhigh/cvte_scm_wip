package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.base.WipBaseService;
import com.cvte.scm.wip.domain.core.ckd.dto.WipMcTaskLineUpdateDTO;
import com.cvte.scm.wip.domain.core.ckd.dto.WipMcTaskSaveDTO;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskLineStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcTaskLineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-04-28
 */
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMcTaskLineService extends WipBaseService<WipMcTaskLineEntity, WipMcTaskLineRepository> {

    @Autowired
    private WipMcTaskLineRepository wipMcTaskLineRepository;

    @Autowired
    private WipMcTaskService wipMcTaskService;

    @Autowired
    private WipMcTaskVersionService wipMcTaskVersionService;

    @Autowired
    private WipMcTaskValidateService wipMcTaskValidateService;

    @Autowired
    private WipMcWfService wipMcWfService;

    public List<WipMcTaskLineEntity> listWipMcTaskLine(WipMcTaskLineQuery query) {
        return wipMcTaskLineRepository.listWipMcTaskLine(query);
    }

    public List<WipMcTaskLineView> listWipMcTaskLineView(WipMcTaskLineQuery query) {
        return wipMcTaskLineRepository.listWipMcTaskLineView(query);
    }

    public List<WipMcTaskLineEntity> batchSave(String taskId, List<WipMcTaskSaveDTO.McLine> mcLines) {
        if (CollectionUtils.isEmpty(mcLines)) {
            return new ArrayList<>();
        }

        if (StringUtils.isBlank(taskId)) {
            throw new ParamsIncorrectException("配料任务id不能为空");
        }

        validateSourceLine(mcLines);

        Set<String> sourceLineIdSet = new HashSet<>();

        List<WipMcTaskLineEntity> wipMcTaskLines = new ArrayList<>();
        mcLines.forEach(el -> {

            el.validate();

            if (sourceLineIdSet.contains(el.getSourceLineId())) {
                throw new ParamsIncorrectException("原始单行id出现重复，请检查");
            }

            WipMcTaskLineEntity wipMcTaskLine = new WipMcTaskLineEntity();
            wipMcTaskLine.setMcTaskId(taskId)
                    .setLineId(UUIDUtils.getUUID())
                    .setItemId(el.getItemId())
                    .setSourceLineNo(el.getSourceLineNo())
                    .setSourceLineId(el.getSourceLineId())
                    .setMcQty(el.getMcQty())
                    .setLineStatus(McTaskLineStatusEnum.NORMAL.getCode());
            EntityUtils.writeStdCrtInfoToEntity(wipMcTaskLine, CurrentContextUtils.getOrDefaultUserId("SCM-WIP"));
            wipMcTaskLines.add(wipMcTaskLine);

            sourceLineIdSet.add(el.getSourceLineId());
        });

         wipMcTaskLineRepository.insertList(wipMcTaskLines);
         return wipMcTaskLines;
    }


    public void batchUpdateBySourceLine(WipMcTaskLineUpdateDTO wipMcTaskLineUpdateDTO) {

        wipMcTaskLineUpdateDTO.validate();
        if (CollectionUtils.isEmpty(wipMcTaskLineUpdateDTO.getUpdateList())) {
            throw new ParamsRequiredException("更新数据不能为空");
        }

        OperatingUser operatingUser = new OperatingUser();
        operatingUser.setId(wipMcTaskLineUpdateDTO.getOptUser());
        operatingUser.setAccount(wipMcTaskLineUpdateDTO.getOptUser());
        operatingUser.setName(wipMcTaskLineUpdateDTO.getOptUser());
        CurrentContext.setCurrentOperatingUser(operatingUser);

        List<String> updateSourceLineIds = new ArrayList<>();

        List<WipMcTaskLineUpdateDTO.UpdateLine> updateLines = wipMcTaskLineUpdateDTO.getUpdateList();
        updateLines.forEach(el -> {
            el.validate();
            updateSourceLineIds.add(el.getSourceLineId());
        });

        List<WipMcTaskView> wipMcTaskViews = wipMcTaskService.listWipMcTaskView(new WipMcTaskQuery().setSourceLineIds(updateSourceLineIds));
        wipMcTaskViews.forEach(el -> wipMcTaskValidateService.validateUpdOptOfCurStatus(el.getStatus()));

        List<WipMcTaskLineEntity> wipMcTaskLines =
                listWipMcTaskLine(new WipMcTaskLineQuery().setSourceLineIds(updateSourceLineIds));
        Map<String, WipMcTaskLineEntity> wipMcTaskLineMap =
                wipMcTaskLines.stream().collect(Collectors.toMap(WipMcTaskLineEntity::getSourceLineId, Function.identity()));


        Set<String> lineIdSet = new HashSet<>();
        List<WipMcTaskLineEntity> updateList = new ArrayList<>();
        for (WipMcTaskLineUpdateDTO.UpdateLine updateLine : updateLines) {
            updateLine.validate();

            if (!wipMcTaskLineMap.containsKey(updateLine.getSourceLineId())) {
                log.error("修改的行数据未在配料任务中找到: lineId={}", updateLine.getSourceLineId());
                throw new ParamsIncorrectException("修改的行数据未在配料任务中找到：" + updateLine.getSourceLineId());
            }

            if (lineIdSet.contains(updateLine.getSourceLineId())) {
                log.error("销售订单行id出现重复, repeatId={}", updateLine.getSourceLineId());
                throw new ParamsIncorrectException("销售订单行id出现重复：" + updateLine.getSourceLineId());
            }

            WipMcTaskLineEntity wipMcTaskLine = wipMcTaskLineMap.get(updateLine.getSourceLineId());
            wipMcTaskLine.setMcQty(updateLine.getMcQty()).setLineStatus(updateLine.getLineStatus()).setItemId(updateLine.getItemId());
            EntityUtils.writeStdUpdInfoToEntity(wipMcTaskLine, wipMcTaskLineUpdateDTO.getOptUser());
            updateList.add(wipMcTaskLine);

            lineIdSet.add(updateLine.getSourceLineId());
        }

        wipMcTaskLineRepository.updateList(updateList);

        List<String> mcTaskIds = wipMcTaskViews.stream().map(WipMcTaskView::getMcTaskId).collect(Collectors.toList());

        // 同步更新版本信息
        wipMcTaskVersionService.batchSync(mcTaskIds);

        // 如果配料任务下所有行都被取消，则配料任务头状态更新为取消
        List<String> canceledTaskIds = updateStatusToCancelIfAllLineCanceled(mcTaskIds);

        // 否则，如果当前处于变更中/取消中状态下，恢复成上一个版本
        mcTaskIds.removeAll(canceledTaskIds);
        wipMcWfService.batchRestorePreStatusIfCurStatusEqualsTo(mcTaskIds, McTaskStatusEnum.CHANGE);
    }



    /**
     * 如果配料任务的所有行都被取消了，则将配料任务状态更新为取消
     *
     * @param mcTaskIds 更新的配料任务
     * @return List
     **/
    public List<String> updateStatusToCancelIfAllLineCanceled(List<String> mcTaskIds) {

        List<String> copyMcTaskIds = new ArrayList<>(mcTaskIds);

        List<String> validTaskIds = wipMcTaskService.listValidTaskIds(mcTaskIds);
        copyMcTaskIds.removeAll(validTaskIds);

        for (String cancelTaskId : copyMcTaskIds) {
            wipMcTaskService.updateStatus(cancelTaskId, McTaskStatusEnum.CANCEL.getCode());
        }
        return copyMcTaskIds;
    }

    private void validateSourceLine(List<WipMcTaskSaveDTO.McLine> mcLines) {
        Set<String> lineIds = mcLines.stream().map(WipMcTaskSaveDTO.McLine::getSourceLineId).collect(Collectors.toSet());
        List<WipMcTaskLineEntity> wipMcTaskLines = listWipMcTaskLine(new WipMcTaskLineQuery().setSourceLineIds(new ArrayList<>(lineIds)));

        for (WipMcTaskLineEntity wipMcTaskLine : wipMcTaskLines) {

            if (lineIds.contains(wipMcTaskLine.getSourceLineId())) {
                throw new ParamsIncorrectException("订单行id【" + wipMcTaskLine.getSourceLineId() + "】已存在开立数据");
            }
        }
    }

}
