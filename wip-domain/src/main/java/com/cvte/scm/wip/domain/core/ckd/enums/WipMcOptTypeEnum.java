package com.cvte.scm.wip.domain.core.ckd.enums;

import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy
 * @date 2020-04-28 11:55
 **/
@Getter
@AllArgsConstructor
public enum WipMcOptTypeEnum {

    MERGE("merge", "合并开立"),

    ALONE("alone", "单独开立");

    private String code;

    private String name;

    public static WipMcOptTypeEnum getByCode(String code) {

        for (WipMcOptTypeEnum wipMcOptTypeEnum : WipMcOptTypeEnum.values()) {

            if (wipMcOptTypeEnum.getCode().equals(code)) {
                return wipMcOptTypeEnum;
            }
        }

        throw new SourceNotFoundException("开立类型错误");
    }
}
