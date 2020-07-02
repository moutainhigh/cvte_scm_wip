package com.cvte.scm.wip.domain.core.changebill.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/1 16:53
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum ChangeBillRecycleEnum implements Codeable {
    DONT_RECYCLE("0", "不回收发料"),
    RECYCLE("1", "回收发料"),
    ;

    private String code, desc;

}
