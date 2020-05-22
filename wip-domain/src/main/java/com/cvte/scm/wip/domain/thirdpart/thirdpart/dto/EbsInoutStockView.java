package com.cvte.scm.wip.domain.thirdpart.thirdpart.dto;

import lombok.Data;

/**
 * @author zy
 * @date 2020-05-11 16:24
 **/
@Data
public class EbsInoutStockView {

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
