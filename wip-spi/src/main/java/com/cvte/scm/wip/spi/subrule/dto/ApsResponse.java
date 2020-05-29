package com.cvte.scm.wip.spi.subrule.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/29 10:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors
public class ApsResponse<T> {

    private String status;

    private String message;

    private T data;

}
