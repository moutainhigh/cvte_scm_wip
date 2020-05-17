package com.cvte.scm.wip.infrastructure.client.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 10:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class EbsResponse<T> {

    private String rtMessage;

    private String rtStatus;

    private T rtData;

}
