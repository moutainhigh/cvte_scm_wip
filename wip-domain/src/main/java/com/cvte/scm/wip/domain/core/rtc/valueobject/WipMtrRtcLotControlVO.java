package com.cvte.scm.wip.domain.core.rtc.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 16:47
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrRtcLotControlVO {

    private String organizationId;

    private String productClass;

    private String mtrClass;

    private String ebsOrganizationId;

    private String optionValue;

    private String optionSystemNo;

    private String optionNo;

    private String optionDict;

    private String optionName;

}
