package com.cvte.scm.wip.app.rtc.status;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 15:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrRtcHeaderReviewDTO {

    private String headerId;

    private String approved;

    private String reviewOpinion;

}
