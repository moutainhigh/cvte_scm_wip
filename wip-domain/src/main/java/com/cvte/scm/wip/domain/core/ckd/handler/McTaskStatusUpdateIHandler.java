package com.cvte.scm.wip.domain.core.ckd.handler;


import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;

/**
 * @author zy
 * @date 2020-04-30 18:41
 **/
public interface McTaskStatusUpdateIHandler {

    void handler(McTaskInfoView mcTaskInfoView);

    void validate(McTaskInfoView mcTaskInfoView);
}
