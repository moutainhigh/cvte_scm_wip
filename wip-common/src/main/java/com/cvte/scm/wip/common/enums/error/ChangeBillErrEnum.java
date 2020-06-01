package com.cvte.scm.wip.common.enums.error;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/1 16:03
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum ChangeBillErrEnum implements Codeable {
    TARGET_LINE_INVALID("400101", "目标投料行无效,指令:"),
    TARGET_LINE_ISSUED("400102", "目标投料行已领料,指令:"),
    PART_MIX("400103", "不允许部分混料,指令:"),
    EXISTS_PRE_INS("400104", "存在未执行的前置指令,批次:"),
    ;

    private String code, desc;

}
