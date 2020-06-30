package com.cvte.scm.wip.spi.rework.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/7 09:55
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class OcsResponseDTO<T> {

    private String success;

    private String message;

    private Integer code;

    private String code_params;

    private T datas;

}
