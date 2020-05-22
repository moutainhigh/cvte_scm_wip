package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.domain.core.ckd.dto.WipMcTaskUnlockDTO;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcWfQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcWfEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcWfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-04-29
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMcWfService {

    @Autowired
    private WipMcWfRepository repository;

    @Autowired
    private WipMcTaskService wipMcTaskService;

    public int insertSelective(WipMcWfEntity entity) {
        return repository.insertSelective(entity);
    }

    public String saveTaskStatus(String taskId, McTaskStatusEnum mcTaskStatusEnum) {

        if (StringUtils.isBlank(taskId) || ObjectUtils.isNull(mcTaskStatusEnum)) {
            throw new ParamsRequiredException("必传参数不能为空");
        }
        WipMcWfEntity wipMcWf = new WipMcWfEntity();
        wipMcWf.setWfId(UUIDUtils.getUUID())
                .setStatus(mcTaskStatusEnum.getCode())
                .setMcTaskId(taskId)
                .setCrtTime(new Date())
                .setCrtUser(CurrentContextUtils.getOrEmptyOperatingUser().getId());

        repository.insertSelective(wipMcWf);
        return wipMcWf.getWfId();
    }

    public WipMcWfEntity getByWfId(String wfId) {

        WipMcWfEntity wipMcWf = repository.selectById(wfId);
        if (ObjectUtils.isNull(wfId)) {
            throw new SourceNotFoundException("获取配料任务状态失败，请联系管理员");
        }
        return wipMcWf;
    }


    public void restorePreStatusIfCurStatusEqualsTo(String mcTaskId, McTaskStatusEnum mcTaskStatusEnum) {

        List<WipMcWfEntity> wipMcWfs = repository.listWipMcWf(new WipMcWfQuery().setMcTaskId(mcTaskId));
        if (CollectionUtils.isEmpty(wipMcWfs) || wipMcWfs.size() < 2) {
            return;
        }

        if (!mcTaskStatusEnum.getCode().equals(wipMcWfs.get(0).getStatus())) {
            return;
        }
        wipMcTaskService.updateStatus(mcTaskId, wipMcWfs.get(1).getStatus());

    }

    public void batchRestorePreStatusIfCurStatusEqualsTo(List<String> mcTaskIds, McTaskStatusEnum mcTaskStatusEnum) {

        for (String mcTaskId : mcTaskIds) {
            restorePreStatusIfCurStatusEqualsTo(mcTaskId, mcTaskStatusEnum);
        }
    }

    public void unlockBySourceLineIds(WipMcTaskUnlockDTO wipMcTaskUnlockDTO) {

        if (StringUtils.isBlank(wipMcTaskUnlockDTO.getOptUser())) {
            throw new ParamsRequiredException("当前处理人不能为空");
        }

        if (CollectionUtils.isEmpty(wipMcTaskUnlockDTO.getSourceLineIds())) {
            throw new ParamsRequiredException("原始单行id不能为空");
        }

        // 设入当前处理人
        OperatingUser operatingUser = new OperatingUser();
        operatingUser.setId(wipMcTaskUnlockDTO.getOptUser());
        operatingUser.setAccount(wipMcTaskUnlockDTO.getOptUser());
        operatingUser.setName(wipMcTaskUnlockDTO.getOptUser());
        CurrentContext.setCurrentOperatingUser(operatingUser);

        List<WipMcTaskView> wipMcTaskViews = wipMcTaskService
                .listWipMcTaskView(new WipMcTaskQuery().setSourceLineIds(wipMcTaskUnlockDTO.getSourceLineIds()));
        List<String> mcTaskIds = wipMcTaskViews.stream().map(WipMcTaskView::getMcTaskId).collect(Collectors.toList());

        batchRestorePreStatusIfCurStatusEqualsTo(mcTaskIds, McTaskStatusEnum.CHANGE);
    }


}
