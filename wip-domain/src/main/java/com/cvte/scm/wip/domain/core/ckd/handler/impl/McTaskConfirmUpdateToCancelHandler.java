package com.cvte.scm.wip.domain.core.ckd.handler.impl;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.ckd.annotation.McTaskStatusAnnotation;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskDeliveryStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.handler.McTaskStatusUpdateIHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zy
 * @date 2020-05-08 15:30
 **/
@Component
@McTaskStatusAnnotation(curStatus = McTaskStatusEnum.CONFIRM, updateToStatusArr = McTaskStatusEnum.CANCEL)
@Transactional(transactionManager = "pgTransactionManager")
public class McTaskConfirmUpdateToCancelHandler implements McTaskStatusUpdateIHandler {

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

        if (StringUtils.isNotBlank(mcTaskInfoView.getDeliveryInStatus())
                && !McTaskDeliveryStatusEnum.CANCELLED.getCode().equals(mcTaskInfoView.getDeliveryInStatus())
                || (StringUtils.isNotBlank(mcTaskInfoView.getDeliveryOutStatus())
                && !McTaskDeliveryStatusEnum.CANCELLED.getCode().equals(mcTaskInfoView.getDeliveryOutStatus()))
        ) {
            throw new ParamsIncorrectException("必须作废已创建的调拨单才可取消");
        }
    }
}
