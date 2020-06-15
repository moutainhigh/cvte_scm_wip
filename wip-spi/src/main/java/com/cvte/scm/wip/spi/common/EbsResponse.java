package com.cvte.scm.wip.spi.common;

import lombok.Data;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/25 18:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class EbsResponse<T> {

    private String rtStatus;

    private String rtMessage;

    private T rtData;

}
