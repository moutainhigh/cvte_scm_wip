package com.cvte.scm.wip.domain.core.ckd.handler.impl;

import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.handler.McTaskStatusUpdateIHandler;
import org.springframework.stereotype.Component;

/**
 * @author zy
 * @date 2020-04-30 18:45
 **/
@Component
public class McTaskStatusUpdateDefaultHandler implements McTaskStatusUpdateIHandler {

    @Override
    public void handler(McTaskInfoView mcTaskInfoView) {
        // nothing to do
    }

    @Override
    public void validate(McTaskInfoView mcTaskInfoView) {
        // noting to do.
    }
}
