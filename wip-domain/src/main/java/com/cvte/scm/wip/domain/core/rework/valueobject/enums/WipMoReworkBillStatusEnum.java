package com.cvte.scm.wip.domain.core.rework.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/24 14:23
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
@AllArgsConstructor
public enum WipMoReworkBillStatusEnum implements Codeable {
    NEW("90060", "新建"),
    SUBMIT("90061", "提交"),
    DEPT_HEAD_CHECK("90062", "部门主管审核"),
    PE_CHECK("90063", "PE审核"),
    PQA_CHECK("90064", "PQA审核"),
    PMC_CHECK("90065", "PMC审核"),
    FACTORY_CHECK("90066", "工厂确认"),
    REJECT("90067", "驳回"),
    INVALID("90068", "作废"),
    ;
    private String code, desc;
}
