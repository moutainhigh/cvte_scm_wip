package com.cvte.scm.wip.domain.core.ckd.dto.view;

import lombok.Data;

/**
 * @author zy
 * @date 2020-05-13 16:16
 **/
@Data
public class WipLineStatusView {

    private String lineId;

    private String sourceLineId;

    private String sourceLineNo;

    private String status;

    private String statusName;

}
