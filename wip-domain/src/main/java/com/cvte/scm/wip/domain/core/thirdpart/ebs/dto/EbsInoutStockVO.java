package com.cvte.scm.wip.domain.core.thirdpart.ebs.dto;

import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.enums.EbsDeliveryStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-05-11 16:24
 **/
@Data
@Accessors(chain = true)
public class EbsInoutStockVO {

    private String ticketNo;

    private String headerId;

    private String status;

    private String statusName;

    private Integer planQty;

    private Integer actualQty;

    private String origSysSourceId;

    private String postedFlag;

    private String cancelledFlag;

    public boolean isCancelledLine() {
        return EbsDeliveryStatusEnum.CANCELLED.getCode().endsWith(status)
                || BooleanEnum.YES.getCode().equals(cancelledFlag);
    }

    public boolean isPosted() {
        return EbsDeliveryStatusEnum.POSTED.getCode().endsWith(status)
                && BooleanEnum.YES.getCode().equals(postedFlag);

    }

}
