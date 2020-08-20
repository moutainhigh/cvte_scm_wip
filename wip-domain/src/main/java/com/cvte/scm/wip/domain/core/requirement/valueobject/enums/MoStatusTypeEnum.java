package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/19 17:36
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum  MoStatusTypeEnum implements Codeable {
    UN_ISSUED("1", "未发放"),
    ISSUED("3", "已发放"),
    CANCEL("7", "已取消"),
    FINISH("12", "已关闭"),
    ;

    private String code, desc;

}
