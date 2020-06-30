package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcReqToTaskEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcReqToTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-04-28
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMcReqToTaskService {

    @Autowired
    private WipMcReqToTaskRepository reqToTaskRepository;

    public void saveTaskRel(String taskId, Set<String> sourceIdSet) {

        if (StringUtils.isBlank(taskId) || CollectionUtils.isEmpty(sourceIdSet)) {
            throw new ParamsIncorrectException("必传参数不能为空");
        }

        List<WipMcReqToTaskEntity> wipMcReqToTasks = reqToTaskRepository.selectList(new WipMcReqToTaskEntity().setMcTaskId(taskId));
        Set<String> existSourceIdSet = wipMcReqToTasks.stream().map(WipMcReqToTaskEntity::getMcReqId).collect(Collectors.toSet());

        List<WipMcReqToTaskEntity> wipMcReqToTaskList = new ArrayList<>();
        for (String sourceId : sourceIdSet) {
            if (existSourceIdSet.contains(sourceId)) {
                continue;
            }

            WipMcReqToTaskEntity wipMcReqToTask = new WipMcReqToTaskEntity();
            wipMcReqToTask.setId(UUIDUtils.getUUID()).setMcTaskId(taskId).setMcReqId(sourceId);
            wipMcReqToTaskList.add(wipMcReqToTask);
        }

        reqToTaskRepository.insertList(wipMcReqToTaskList);
    }


    public List<WipMcReqToTaskEntity> selectList(WipMcReqToTaskEntity queryEntity) {
        return reqToTaskRepository.selectList(queryEntity);
    }

}
