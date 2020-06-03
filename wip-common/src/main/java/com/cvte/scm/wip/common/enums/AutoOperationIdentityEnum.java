package com.cvte.scm.wip.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/29 15:12
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum AutoOperationIdentityEnum implements Codeable {
    WIP("SCM-WIP", "WIP自动变更"),
    EBS("EBS", "EBS变更同步"),
    ;
    private String code, desc;
}
