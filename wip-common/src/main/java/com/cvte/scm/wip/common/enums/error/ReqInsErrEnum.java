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
    INVALID_INS("4001001", "无效的指令"),
    TARGET_LINE_INVALID("4001002", "目标投料行不存在"),
    TARGET_LINE_ISSUED("4001003", "目标投料行已领料"),
    PART_MIX("4001004", "不允许部分混料"),
    EXISTS_PRE_INS("4001005", "存在未执行的前置更改单"),
    ADD_VALID_QTY("4001006", "新增投料行单位用量不可为空"),
    TARGET_LOT_INVALID("4001007", "目标批次不存在"),
    KEY_NULL("4001008", "指令缺失关键索引"),
    INS_IMMUTABLE("4001009", "已执行或作废的投料指令不允许变更"),
    WRITE_BACK_ERR("4001010", "回写EBS返工单时发生错误"),
    TARGET_REQ_INVALID("4001011", "目标投料单不存在"),
    ;

    private String code, desc;

}
