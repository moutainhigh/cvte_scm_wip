package com.cvte.scm.wip.domain.core.rtc.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/10 14:34
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrRtcLineBuildVO {

    private String lineId;

    private String headerId;

    private BigDecimal reqQty;

    private String invpNo;

    private BigDecimal issuedQty;

    private String supplier;

    private String serialNo;

    private String badReason;

    private String badDesc;

}
