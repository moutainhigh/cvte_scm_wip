package com.cvte.scm.wip.common.enums.error;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/2 18:55
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum  ReworkBillErrEnum implements Codeable {
    SYNC_TO_EBS("4002001", "同步至EBS失败"),
    ;

    private String code, desc;

}
