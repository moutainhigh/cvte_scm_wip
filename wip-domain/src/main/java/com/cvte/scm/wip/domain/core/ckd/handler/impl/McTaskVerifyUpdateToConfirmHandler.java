package com.cvte.scm.wip.domain.core.ckd.handler.impl;

import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.ckd.annotation.McTaskStatusAnnotation;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.handler.McTaskStatusUpdateIHandler;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zy
 * @date 2020-04-30 18:52
 **/
@Component
@McTaskStatusAnnotation(curStatus = McTaskStatusEnum.VERIFY, updateToStatusArr = McTaskStatusEnum.CONFIRM)
@Transactional(transactionManager = "pgTransactionManager")
public class McTaskVerifyUpdateToConfirmHandler implements McTaskStatusUpdateIHandler {

    @Autowired
    private WipMcTaskVersionService wipMcTaskVersionService;

    @Override
    public void handler(McTaskInfoView mcTaskInfoView) {

        if (ObjectUtils.isNull(mcTaskInfoView)
            || StringUtils.isBlank(mcTaskInfoView.getMcTaskId())) {
            throw new ParamsRequiredException("必传参数不能为空");
        }

        // 更新当前版本信息
        wipMcTaskVersionService.sync(mcTaskInfoView.getMcTaskId());
    }

    @Override
    public void validate(McTaskInfoView mcTaskInfoView) {
        // noting to do.
    }
}
