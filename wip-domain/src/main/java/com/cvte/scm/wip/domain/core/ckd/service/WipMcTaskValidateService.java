package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.csb.core.exception.client.forbiddens.NoOperationRightException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.utils.EnumUtils;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.TransactionTypeNameEnum;
import com.cvte.scm.wip.domain.core.ckd.utils.McTaskStatusUtils;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.enums.EbsDeliveryStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author zy
 * @date 2020-05-07 11:58
 **/
@Slf4j
@Service
public class WipMcTaskValidateService {

    @Autowired
    private WipMcTaskService wipMcTaskService;

    public void validateUpdStatusTo(String curStatus, String updStatusTo) {

        if (StringUtils.isBlank(curStatus, updStatusTo)) {
            throw new ParamsRequiredException("必传参数不能为空");
        }

        boolean canUpdateTo = McTaskStatusUtils.canUpdateStatusTo(curStatus, updStatusTo);
        if (!canUpdateTo) {
            McTaskStatusEnum curStatusEnum = EnumUtils.getByCode(curStatus, McTaskStatusEnum.class);
            log.error("不能将{}的配料任务状态更改为{}", curStatus, updStatusTo);
            throw new NoOperationRightException("没有对【" + curStatusEnum.getValue() + "】状态的配料任务进行该操作的权限");
        }
    }

    public void validateUpdVersion(String mcTaskId) {
        if (StringUtils.isBlank(mcTaskId)) {
            throw new ParamsRequiredException("配料任务id不能为空");
        }

        McTaskInfoView mcTaskInfoView = wipMcTaskService.getMcTaskInfoView(mcTaskId);

        if (!McTaskStatusUtils.canUpdate(mcTaskInfoView.getStatus())) {
            McTaskStatusEnum mcTaskStatusEnum = EnumUtils.getByCode(mcTaskInfoView.getStatus(), McTaskStatusEnum.class);
            throw new ParamsIncorrectException("不能更新【" + mcTaskStatusEnum.getValue() + "】状态的配料任务版本信息");
        }
    }

    public McTaskInfoView validateUpdOpt(String mcTaskId) {

        if (StringUtils.isBlank(mcTaskId)) {
            log.error("必传参数不能为空, mcTaskId={}", mcTaskId);
            throw new ParamsRequiredException("配料任务id不能为空");
        }

        McTaskInfoView mcTaskInfoView = wipMcTaskService.getMcTaskInfoView(mcTaskId);
        validateUpdOptOfCurStatus(mcTaskInfoView.getStatus());
        return mcTaskInfoView;
    }

    public void validateUpdOptOfCurStatus(String curStatus) {
        if (!McTaskStatusUtils.canUpdate(curStatus)) {
            McTaskStatusEnum mcTaskStatusEnum = EnumUtils.getByCode(curStatus, McTaskStatusEnum.class);
            throw new ParamsIncorrectException("不能更新【" + mcTaskStatusEnum.getValue() + "】状态的配料任务");
        }
    }


    public void validateInoutStock(TransactionTypeNameEnum transactionTypeNameEnum, McTaskInfoView mcTaskInfoView) {

        if (ObjectUtils.isNull(mcTaskInfoView) || StringUtils.isBlank(mcTaskInfoView.getStatus())) {
            throw new ParamsRequiredException("获取配料任务状态错误");
        }

        if (EnumUtils.isIn(mcTaskInfoView.getStatus(),
                McTaskStatusEnum.CREATE,
                McTaskStatusEnum.VERIFY,
                McTaskStatusEnum.REJECT,
                McTaskStatusEnum.CANCEL,
                McTaskStatusEnum.CLOSE,
                McTaskStatusEnum.CHANGE)) {
            McTaskStatusEnum mcTaskStatusEnum = EnumUtils.getByCode(mcTaskInfoView.getStatus(), McTaskStatusEnum.class);
            throw new ParamsIncorrectException("不可对" + mcTaskStatusEnum.getValue() + "状态的配料进行调拨操作");
        }

        switch (transactionTypeNameEnum) {
            case OUT:
                if (StringUtils.isNotBlank(mcTaskInfoView.getDeliveryOutStatus())
                    && !EbsDeliveryStatusEnum.CANCELLED.getCode().equals(mcTaskInfoView.getDeliveryOutStatus())) {
                    throw new ParamsIncorrectException("调拨出库单已存在");
                }
                break;
            case IN:
                if (StringUtils.isNotBlank(mcTaskInfoView.getDeliveryInStatus())
                        && !EbsDeliveryStatusEnum.CANCELLED.getCode().equals(mcTaskInfoView.getDeliveryInStatus())) {
                    throw new ParamsIncorrectException("调拨入库单已存在");
                }

                if (!EbsDeliveryStatusEnum.POSTED.getCode().equals(mcTaskInfoView.getDeliveryOutStatus())) {
                    throw new ParamsIncorrectException("调拨出库单未过账不允许创建调拨入库");
                }

                // 头表已过账，行表含有未过账数据，也不能创建入库单
                break;
            default:
                throw new ParamsIncorrectException("不支持的调拨类型");
        }

    }

}
