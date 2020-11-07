package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : jf
 * Date    : 2019.01.06
 * Time    : 09:41
 * Email   ：jiangfeng7128@cvte.com
 */
@Getter
@AllArgsConstructor
public enum ProcessingStatusEnum implements Codeable {
    PENDING("0", "待处理"),
    SOLVED("1", "已处理"),
    EXCEPTION("2", "处理异常"),
    UNHANDLED("3", "不处理"),
    CLOSE("130", "作废"),
    ;

    private static final Collection<String> unProcessStatus;

    static {
        Set<String> unPostStatusSet = new HashSet<>();
        unPostStatusSet.add(PENDING.code);
        unPostStatusSet.add(EXCEPTION.code);
        unProcessStatus = unPostStatusSet;
    }

    public static Collection<String> getUnProcessStatus() {
        return unProcessStatus;
    }

    private String code, desc;
}