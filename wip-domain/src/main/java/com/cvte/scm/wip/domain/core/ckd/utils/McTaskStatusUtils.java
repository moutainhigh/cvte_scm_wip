package com.cvte.scm.wip.domain.core.ckd.utils;


import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zy
 * @date 2020-04-30 16:24
 **/
public class McTaskStatusUtils {


    private static final Map<String, List<String>> STATUS_FLOW_MAP = new HashMap();

    static {

        /* 初始化生命周期 */
        // 新建 -> 已审批、已取消、变更中/取消中
        STATUS_FLOW_MAP.put(McTaskStatusEnum.CREATE.getCode(), Arrays.asList(McTaskStatusEnum.VERIFY.getCode(), McTaskStatusEnum.CANCEL.getCode(), McTaskStatusEnum.CHANGE.getCode()));
        // 已审批 -> 已驳回、工厂已确认、已取消、变更中/取消中
        STATUS_FLOW_MAP.put(McTaskStatusEnum.VERIFY.getCode(), Arrays.asList(McTaskStatusEnum.REJECT.getCode(), McTaskStatusEnum.CONFIRM.getCode(), McTaskStatusEnum.CANCEL.getCode(), McTaskStatusEnum.CHANGE.getCode()));
        // 工厂已确认 -> 已驳回、已完成、已取消
        STATUS_FLOW_MAP.put(McTaskStatusEnum.CONFIRM.getCode(), Arrays.asList(McTaskStatusEnum.FINISH.getCode(), McTaskStatusEnum.REJECT.getCode(), McTaskStatusEnum.CANCEL.getCode()));
        // 已完成 -> 已关闭
        STATUS_FLOW_MAP.put(McTaskStatusEnum.FINISH.getCode(), Arrays.asList(McTaskStatusEnum.CLOSE.getCode()));
        // 已驳回 -> 已审批、变更中/取消中
        STATUS_FLOW_MAP.put(McTaskStatusEnum.REJECT.getCode(), Arrays.asList(McTaskStatusEnum.VERIFY.getCode(), McTaskStatusEnum.CHANGE.getCode()));

        // 变更中/取消中 -> 新建、已审批、工厂已确认、已驳回
        STATUS_FLOW_MAP.put(McTaskStatusEnum.CHANGE.getCode(), Arrays.asList(McTaskStatusEnum.CREATE.getCode(), McTaskStatusEnum.VERIFY.getCode(), McTaskStatusEnum.REJECT.getCode(), McTaskStatusEnum.CANCEL.getCode()));

    }


    private McTaskStatusUtils() {

    }

    /**
     * 判断当前状态的配料任务信息是否可以更新
     *
     * @param curStatus
     * @return boolean
     **/
    public static boolean canUpdate(String curStatus) {
        boolean canUpdate = false;
        if (McTaskStatusEnum.CREATE.getCode().equals(curStatus)
                || McTaskStatusEnum.VERIFY.getCode().equals(curStatus)
                || McTaskStatusEnum.REJECT.getCode().equals(curStatus)
                || McTaskStatusEnum.CHANGE.getCode().equals(curStatus)) {
            canUpdate = true;
        }
        return canUpdate;
    }

    /**
     * 校验状态变更是否合法
     *
     * @param curStatus
     * @param updateToStatus
     * @return boolean
     **/
    public static boolean canUpdateStatusTo(String curStatus, String updateToStatus) {

        if (!STATUS_FLOW_MAP.containsKey(curStatus)) {
            return false;
        }

        List<String> canUpdateToStatus = STATUS_FLOW_MAP.get(curStatus);
        for (String status : canUpdateToStatus) {
            if (status.equals(updateToStatus)) {
                return true;
            }
        }
        return false;
    }


}
