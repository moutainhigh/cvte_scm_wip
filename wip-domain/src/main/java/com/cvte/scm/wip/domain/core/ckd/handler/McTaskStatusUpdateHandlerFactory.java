package com.cvte.scm.wip.domain.core.ckd.handler;


import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.handler.impl.McTaskStatusUpdateDefaultHandler;

import java.util.EnumMap;

/**
 * @author zy
 * @date 2020-04-30 18:44
 **/
public class McTaskStatusUpdateHandlerFactory {

    private McTaskStatusUpdateHandlerFactory() {

    }

    private static EnumMap<McTaskStatusEnum, EnumMap<McTaskStatusEnum, McTaskStatusUpdateIHandler>> cache = new EnumMap<>(McTaskStatusEnum.class);


    public static McTaskStatusUpdateIHandler getInstance(McTaskStatusEnum curStatus, McTaskStatusEnum updateToStatus) {

        if (!cache.containsKey(curStatus) || !cache.get(curStatus).containsKey(updateToStatus)) {
            return new McTaskStatusUpdateDefaultHandler();
        }

        return cache.get(curStatus).get(updateToStatus);
    }

    public static void put(McTaskStatusEnum curStatus, McTaskStatusEnum updateToStatus, McTaskStatusUpdateIHandler hook) {

        if (!cache.containsKey(curStatus)) {
            cache.put(curStatus, new EnumMap<>(McTaskStatusEnum.class));
        }

        cache.get(curStatus).put(updateToStatus, hook);
    }
}
