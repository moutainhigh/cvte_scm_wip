package com.cvte.scm.wip.domain.core.ckd.handler.impl;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.core.ckd.annotation.McTaskStatusAnnotation;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineView;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskLineStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.handler.McTaskStatusUpdateIHandler;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskLineService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author zy
 * @date 2020-05-08 15:30
 **/
@Component
@McTaskStatusAnnotation(curStatus = McTaskStatusEnum.FINISH, updateToStatusArr = {McTaskStatusEnum.CANCEL, McTaskStatusEnum.REJECT})
@Transactional(transactionManager = "pgTransactionManager")
public class McTaskFinishUpdateToCancelHandler implements McTaskStatusUpdateIHandler {

    @Autowired
    private WipMcTaskLineService wipMcTaskLineService;
    @Autowired
    private WipMcTaskValidateService validateService;


    @Override
    public void handler(McTaskInfoView mcTaskInfoView) {

        // noting to do
    }

    /**
     * 驳回工厂确认状态的配料任务，需要校验是否存在取消以外的调拨单
     *
     * @return void
     **/
    @Override
    public void validate(McTaskInfoView mcTaskInfoView) {
        if (ObjectUtils.isNull(mcTaskInfoView)) {
            throw new ParamsRequiredException("配料任务不能为空");
        }

        List<WipMcTaskLineView> wipMcTaskLineViews = wipMcTaskLineService.listWipMcTaskLineView(new WipMcTaskLineQuery()
                .setTaskIds(Arrays.asList(mcTaskInfoView.getMcTaskId()))
                .setLineStatus(McTaskLineStatusEnum.NORMAL.getCode()));

        for (WipMcTaskLineView wipMcTaskLineView : wipMcTaskLineViews) {
            if (validateService.hasDeliveryBill(wipMcTaskLineView.getDeliveryOutLineStatus()) && !validateService.isReturnMaterial(wipMcTaskLineView)) {
                throw new ParamsIncorrectException("调拨单需已作废或已退料才可进行该操作");
            }
        }
    }





}
