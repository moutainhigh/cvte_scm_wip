package com.cvte.scm.wip.domain.core.rtc.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/11 09:05
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrRtcAssignBuildVO {

    private String assignId;

    private String lineId;

    private String headerId;

    private String organizationId;

    private String factoryId;

    private String mtrLotNo;

    private String invpNo;

    private BigDecimal assignQty;

    private BigDecimal issuedQty;

    private String sourceOrderId;

    private String changeType;

    private String lotControlType;

}
