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
public enum ReqInsErrEnum implements Codeable {
    INVALID_INS("4001001", "无效的指令,"),
    TARGET_LINE_INVALID("4001002", "目标投料行无效,指令:"),
    TARGET_LINE_ISSUED("4001003", "目标投料行已领料,指令:"),
    PART_MIX("4001004", "不允许部分混料,指令:"),
    EXISTS_PRE_INS("4001005", "存在未执行的前置指令,批次:"),
    ADD_VALID_QTY("4001006", "新增投料行单位用量不可为空,指令:"),
    ADD_LOT_NULL("4001007", "批次为空或不存在,指令:"),
    KEY_NULL("4001008", "指令缺失关键索引,"),
    ;

    private String code, desc;

}
