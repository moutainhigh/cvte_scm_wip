package com.cvte.scm.wip.spi.common;

import lombok.Data;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/30 16:11
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class CommonResponse<T> {

    private String status;

    private String message;

    private T data;

}
