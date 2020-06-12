package com.cvte.scm.wip.domain.core.ckd.enums;

import com.cvte.scm.wip.common.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * （create:创建，verify：审批，reject：驳回，cancel:取消，confirm：工厂已确认，finish：完成，close：关闭）
 * @author zy
 * @date 2020-04-28 11:35
 **/
@Getter
@AllArgsConstructor
public enum McTaskStatusEnum implements CodeEnum {

    CREATE("create", "新建", "创建审批流程"),
    VERIFY("verify", "生管已审核", "生管审核"),
    REJECT("reject", "生管已驳回", "生管驳回"),
    CANCEL("cancel", "已取消", "流程取消"),
    CONFIRM("confirm", "工厂已确认", "工厂确认"),
    FINISH("finish", "完成", "流程完成"),
    CLOSE("close", "关闭", "流程关闭"),
    CHANGE("change", "变更中/取消中", "发起变更审核");

    private String code;

    private String value;

    private String optName;

}
