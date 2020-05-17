package com.cvte.scm.wip.domain.core.requirement.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工序号枚举类
 */
@Getter
@AllArgsConstructor
public enum OperationSeqNumEnum implements Codeable {

    TEN("10", "十"),
    TWENTY("20", "二十"),
    THIRTY("30", "三十"),
    FORTY("40", "四十"),
    FIFTY("50", "五十");

    private String code, desc;
}