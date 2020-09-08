package com.cvte.scm.wip.domain.core.rtc.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/8 16:16
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class RtcLineQueryVO {

    private String lineId;

    private String moId;

    private String headerId;

    private String organizationId;

    private String itemId;

    private String itemNo;

    private String moLotNo;

    private String wkpNo;

    private String invpNo;

    private String billType;

    private Collection<String> statusColl;

}
