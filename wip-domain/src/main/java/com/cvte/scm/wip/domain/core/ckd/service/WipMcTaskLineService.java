package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.constants.CommonUserConstant;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.base.WipBaseService;
import com.cvte.scm.wip.domain.common.log.constant.LogModuleConstant;
import com.cvte.scm.wip.domain.common.log.dto.WipLogDTO;
import com.cvte.scm.wip.domain.common.log.service.WipOperationLogService;
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
import com.cvte.scm.wip.domain.core.scm.dto.query.MdItemQuery;
import com.cvte.scm.wip.domain.core.scm.service.ScmViewCommonService;
import com.cvte.scm.wip.domain.core.scm.vo.MdItemVO;
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

    @Autowired
    private ScmViewCommonService scmViewCommonService;

    @Autowired
    private WipOperationLogService wipOperationLogService;

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
        List<String> itemCodes = new ArrayList<>();

        List<WipMcTaskLineUpdateDTO.UpdateLine> updateLines = wipMcTaskLineUpdateDTO.getUpdateList();
        updateLines.forEach(el -> {
            el.validate();
            updateSourceLineIds.add(el.getSourceLineId());
            itemCodes.add(el.getItemCode());
        });

        List<WipMcTaskView> wipMcTaskViews = wipMcTaskService.listWipMcTaskView(new WipMcTaskQuery().setSourceLineIds(updateSourceLineIds));
        wipMcTaskViews = wipMcTaskViews.stream()
                .filter(el -> !McTaskStatusEnum.CANCEL.getCode().equals(el.getStatus()))
                .collect(Collectors.toList());
        wipMcTaskViews.forEach(el -> wipMcTaskValidateService.validateUpdOptOfCurStatus(el.getStatus()));
        Set<String> mcTaskIdSet = wipMcTaskViews.stream().map(WipMcTaskView::getMcTaskId).collect(Collectors.toSet());

        List<WipMcTaskLineEntity> wipMcTaskLines =
                listWipMcTaskLine(new WipMcTaskLineQuery().setSourceLineIds(updateSourceLineIds));
        Map<String, WipMcTaskLineEntity> wipMcTaskLineMap =
                wipMcTaskLines.stream().collect(Collectors.toMap(WipMcTaskLineEntity::getSourceLineId, Function.identity()));
        Map<String, MdItemVO> mdItemVOMap = generateCodeAndItemVoMap(itemCodes);


        Set<String> lineIdSet = new HashSet<>();
        List<WipMcTaskLineEntity> updateList = new ArrayList<>();
        for (WipMcTaskLineUpdateDTO.UpdateLine updateLine : updateLines) {
            updateLine.validate();

            WipMcTaskLineEntity wipMcTaskLine = wipMcTaskLineMap.get(updateLine.getSourceLineId());
            if (!wipMcTaskLineMap.containsKey(updateLine.getSourceLineId())
                || !mcTaskIdSet.contains(wipMcTaskLine.getMcTaskId())) {
                // 静默处理，CRM端不能知道是否开立了配料任务
                log.error("修改的行数据未在配料任务中找到: lineId={}", updateLine.getSourceLineId());
                continue;
            }

            if (lineIdSet.contains(updateLine.getSourceLineId())) {
                log.error("销售订单行id出现重复, repeatId={}", updateLine.getSourceLineId());
                throw new ParamsIncorrectException("销售订单行id出现重复：" + updateLine.getSourceLineId());
            }

            if (!mdItemVOMap.containsKey(updateLine.getItemCode())) {
                log.error("物料{}不存在", updateLine.getItemCode());
                throw new SourceNotFoundException("获取物料信息错误：itemCode=" + updateLine.getItemCode());
            }


            wipMcTaskLine.setMcQty(updateLine.getMcQty())
                    .setLineStatus(updateLine.getLineStatus())
                    .setItemId(mdItemVOMap.get(updateLine.getItemCode()).getInventoryItemId());
            EntityUtils.writeStdUpdInfoToEntity(wipMcTaskLine, wipMcTaskLineUpdateDTO.getOptUser());
            updateList.add(wipMcTaskLine);

            lineIdSet.add(updateLine.getSourceLineId());
        }

        this.updateAndLogList(updateList);

        List<String> mcTaskIds = new ArrayList<>(mcTaskIdSet);

        // 同步更新版本信息
        wipMcTaskVersionService.batchSync(mcTaskIds);

        // 如果配料任务下所有行都被取消，则配料任务头状态更新为取消
        List<String> canceledTaskIds = updateStatusToCancelIfAllLineCanceled(mcTaskIds);

        // 否则，如果当前处于变更中/取消中状态下，恢复成上一个版本
        mcTaskIds.removeAll(canceledTaskIds);
        wipMcWfService.batchRestorePreStatusIfCurStatusEqualsTo(mcTaskIds, McTaskStatusEnum.CHANGE);
    }

    /**
     * 更新配料任务行并记录日志
     *
     * @param updateList
     * @return void
     **/
    public void updateAndLogList(List<WipMcTaskLineEntity> updateList) {
        wipMcTaskLineRepository.updateList(updateList);

        Set<String> mcTaskSet = updateList.stream().map(WipMcTaskLineEntity::getMcTaskId).collect(Collectors.toSet());
        List<WipLogDTO> wipLogDTOList = new ArrayList<>();
        mcTaskSet.forEach(el -> {
            WipLogDTO wipLogDTO = new WipLogDTO();
            wipLogDTO.setReferenceId(el)
                    .setModule(LogModuleConstant.CKD)
                    .setOperation("更新配料任务行");
            wipLogDTOList.add(wipLogDTO);
        });
        wipOperationLogService.addLogs(wipLogDTOList, CurrentContext.getCurrentOperatingUser());
    }


    /**
     * 如果配料任务的所有行都被取消了，则将配料任务状态更新为取消
     *
     * @param mcTaskIds 更新的配料任务
     * @return List
     **/
    public List<String> updateStatusToCancelIfAllLineCanceled(List<String> mcTaskIds) {
        if (CollectionUtils.isEmpty(mcTaskIds)) {
            return new ArrayList<>();
        }

        List<String> copyMcTaskIds = new ArrayList<>(mcTaskIds);

        List<String> validTaskIds = wipMcTaskService.listValidTaskIds(mcTaskIds);
        copyMcTaskIds.removeAll(validTaskIds);

        for (String cancelTaskId : copyMcTaskIds) {
            wipMcTaskService.updateStatus(cancelTaskId, McTaskStatusEnum.CANCEL.getCode(), false);
        }
        return copyMcTaskIds;
    }


    public void refreshUpdTime(List<String> mcTaskLineIds) {
        if (CollectionUtils.isEmpty(mcTaskLineIds)) {
            return;
        }
        List<WipMcTaskLineEntity> updList = new ArrayList<>();
        for (String mcTaskLineId : mcTaskLineIds) {
            WipMcTaskLineEntity entity = new WipMcTaskLineEntity();
            entity.setLineId(mcTaskLineId);
            EntityUtils.writeStdUpdInfoToEntity(entity, CurrentContextUtils.getOrDefaultUserId(CommonUserConstant.SCM_WIP));
            updList.add(entity);
        }
        updateList(updList);
    }

    private void validateSourceLine(List<WipMcTaskSaveDTO.McLine> mcLines) {
        Set<String> lineIds = mcLines.stream().map(WipMcTaskSaveDTO.McLine::getSourceLineId).collect(Collectors.toSet());
        List<WipMcTaskLineEntity> wipMcTaskLines = listWipMcTaskLine(new WipMcTaskLineQuery().setSourceLineIds(new ArrayList<>(lineIds)));

        Set<String> cancelledTaskIds = new HashSet<>();
        if (CollectionUtils.isNotEmpty(wipMcTaskLines)) {
            List<WipMcTaskView> wipMcTaskViews = wipMcTaskService.listWipMcTaskView(
                    new WipMcTaskQuery().setStatus(McTaskStatusEnum.CANCEL.getCode()).setTaskIds(
                            wipMcTaskLines.stream().map(WipMcTaskLineEntity::getMcTaskId).collect(Collectors.toList())));
            cancelledTaskIds = wipMcTaskViews.stream().map(WipMcTaskView::getMcTaskId).collect(Collectors.toSet());
        }


        for (WipMcTaskLineEntity wipMcTaskLine : wipMcTaskLines) {

            if (lineIds.contains(wipMcTaskLine.getSourceLineId()) && !cancelledTaskIds.contains(wipMcTaskLine.getMcTaskId())) {
                throw new ParamsIncorrectException("订单行id【" + wipMcTaskLine.getSourceLineId() + "】已存在开立数据");
            }
        }
    }


    /**
     *
     *
     * @param itemCodes
     * @return java.util.Map<java.lang.String,com.cvte.scm.wip.domain.core.scm.vo.MdItemVO>
     **/
    private Map<String, MdItemVO> generateCodeAndItemVoMap(List<String> itemCodes) {
        if (CollectionUtils.isEmpty(itemCodes)) {
            return new HashMap<>();
        }
        List<MdItemVO> mdItemVOS = scmViewCommonService.listMdItemVO(new MdItemQuery().setItemCodes(itemCodes));
        return mdItemVOS.stream().collect(Collectors.toMap(MdItemVO::getItemCode, Function.identity()));
    }

}
