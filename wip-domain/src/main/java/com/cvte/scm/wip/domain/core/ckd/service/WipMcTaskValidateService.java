package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.csb.core.exception.client.forbiddens.NoOperationRightException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.utils.EnumUtils;
import com.cvte.scm.wip.domain.common.attachment.constants.AttReferenceTypeConstant;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentQuery;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentVO;
import com.cvte.scm.wip.domain.common.attachment.service.WipAttachmentService;
import com.cvte.scm.wip.domain.common.sys.user.repository.SysUserRepository;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineView;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskDeliveryStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.TransactionTypeNameEnum;
import com.cvte.scm.wip.domain.core.ckd.utils.McTaskStatusUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author zy
 * @date 2020-05-07 11:58
 **/
@Slf4j
@Service
public class WipMcTaskValidateService {

    // 销管角色编码
    @Value("${wip.role.code.sales}")
    private String salesRoleCode;

    @Autowired
    private WipMcTaskService wipMcTaskService;

    @Autowired
    private WipAttachmentService wipAttachmentService;

    @Autowired
    private SysUserRepository sysUserRepository;

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


    public void validateInoutStock(TransactionTypeNameEnum transactionTypeNameEnum, McTaskInfoView mcTaskInfoView, List<WipMcTaskLineView> wipMcTaskLineViews) {

        if (ObjectUtils.isNull(mcTaskInfoView) || StringUtils.isBlank(mcTaskInfoView.getStatus())) {
            throw new ParamsRequiredException("获取配料任务状态错误");
        }

        if (EnumUtils.isIn(mcTaskInfoView.getStatus(),
                McTaskStatusEnum.CREATE,
                McTaskStatusEnum.VERIFY,
                McTaskStatusEnum.REJECT,
                McTaskStatusEnum.CANCEL,
                McTaskStatusEnum.CHANGE)) {
            McTaskStatusEnum mcTaskStatusEnum = EnumUtils.getByCode(mcTaskInfoView.getStatus(), McTaskStatusEnum.class);
            throw new ParamsIncorrectException("不可对" + mcTaskStatusEnum.getValue() + "状态的配料进行调拨操作");
        }

        for (WipMcTaskLineView wipMcTaskLineView : wipMcTaskLineViews) {
            switch (transactionTypeNameEnum) {
                case OUT:
                    // 对于部分客户的配料任务，需要上传唛头才允许创建调拨出库单
                    if (wipMcTaskService.isSpecClient(mcTaskInfoView.getMcTaskId()) && !hasAttachment(mcTaskInfoView.getMcTaskId())) {
                        throw new ParamsIncorrectException("需要销管上传唛头后，才能创建调拨单");
                    }
                    // 行数据调拨单不存在/已取消，才可创建
                    if (!isCancelled(wipMcTaskLineView.getDeliveryOutLineStatus()) && !isReturnMaterial(wipMcTaskLineView) ) {
                        throw new ParamsIncorrectException("含有调拨出库单已存在的行，需要取消调拨出库单或退料单过账后才可重新调拨出库");
                    }
                    break;
                case IN:
                    // 行数据调拨单不存在/已取消，且行出库单对应的调拨单行已过账才可创建
                    if (!isCancelled(wipMcTaskLineView.getDeliveryInLineStatus())) {
                        throw new ParamsIncorrectException("含有调拨入库单已存在的行，请检查");
                    }
                    if (isReturnMaterial(wipMcTaskLineView)) {
                        throw new ParamsIncorrectException("含有生产退料单的行，请检查");
                    }
                    if (!McTaskDeliveryStatusEnum.POSTED.getCode().equals(wipMcTaskLineView.getDeliveryOutLineStatus())) {
                        throw new ParamsIncorrectException("含有调拨出库单未过账的行，请检查");
                    }
                    break;
                case RETURN_OUT_MATERIAL:
                    // 退料单不存在/已取消，才可以创建
                    if (StringUtils.isNotBlank(wipMcTaskLineView.getDeliveryRmLineStatus())
                            && !McTaskDeliveryStatusEnum.CANCELLED.getCode().equals(wipMcTaskLineView.getDeliveryRmLineStatus())) {
                        throw new ParamsIncorrectException("含有退料单已存在的行，请检查");
                    }

                    // 出库单全部过账，且未创建入库单才允许创建退料单
                    if (!McTaskDeliveryStatusEnum.POSTED.getCode().equals(wipMcTaskLineView.getDeliveryOutLineStatus())) {
                        throw new ParamsIncorrectException("含有调拨出库单未过账的行，请检查");
                    } else if (StringUtils.isNotBlank(wipMcTaskLineView.getDeliveryInLineStatus())
                            && !McTaskDeliveryStatusEnum.CANCELLED.getCode().equals(wipMcTaskLineView.getDeliveryInLineStatus())) {
                        throw new ParamsIncorrectException("含有调拨入库单已存在的行，请检查");
                    }
                    break;
                default:
                    throw new ParamsIncorrectException("不支持的调拨类型");
            }
        }

    }

    private boolean isCancelled(String status) {
        return StringUtils.isNotBlank(status) && McTaskDeliveryStatusEnum.CANCELLED.getCode().equals(status);
    }

    private boolean isReturnMaterial(WipMcTaskLineView wipMcTaskLineView) {
        return StringUtils.isNotBlank(wipMcTaskLineView.getDeliveryRmLineStatus())
                && McTaskDeliveryStatusEnum.POSTED.getCode().equals(wipMcTaskLineView.getDeliveryRmLineStatus())
                && StringUtils.isNotBlank(wipMcTaskLineView.getDeliveryRmLineSource())
                && wipMcTaskLineView.getDeliveryRmLineSource().equals(wipMcTaskLineView.getDeliveryOutStockLineId());
    }


    /**
     * 判断是否有销管上传的唛头
     *
     * @param mcTaskId
     * @return boolean
     **/
    private boolean hasAttachment(String mcTaskId) {
        if (StringUtils.isBlank(mcTaskId)) {
            return false;
        }

        List<String> userIds = sysUserRepository.listRoleUserIds(salesRoleCode);
        if (CollectionUtils.isEmpty(userIds)) {
            return false;
        }

        List<AttachmentVO> attachmentVOS = wipAttachmentService.listAttachmentView(
                new AttachmentQuery().setReferenceId(mcTaskId).setReferenceType(AttReferenceTypeConstant.CKD).setCrtUsers(userIds));
        return CollectionUtils.isNotEmpty(attachmentVOS);
    }

}
