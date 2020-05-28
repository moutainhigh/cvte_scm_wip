package com.cvte.scm.wip.domain.core.thirdpart.ebs.dto;

import lombok.Data;

/**
 * @author zy
 * @date 2020-05-11 16:24
 **/
@Data
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

}
