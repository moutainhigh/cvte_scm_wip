package com.cvte.scm.wip.common.enums.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/24 14:44
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum ReqLineErrEnum {
    ORG_NON_NULL("4003001", "组织不可为空"),
    SYNC_FAILED("4003002", "同步数据到EBS失败"),
    ;

    private String code, desc;

}
