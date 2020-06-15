package com.cvte.scm.wip.spi.changebill.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/11 12:16
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ChangeBillWriteBackDTO {

    private String billNo;

    private String billType;

    private String executeCode;

    private String executeMessage;

    private String userNo;

}
