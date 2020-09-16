package com.cvte.scm.wip.domain.common.patch.enums;

import lombok.Getter;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/28 9:17 上午
 */
@Getter
public enum PatchEnum {

    /**
     * 新建
     */
    NEW("10"),
    /**
     * 已提交
     */
    COMMIT("20"),
    /**
     * 已确认
     */
    CONFIRM("30"),
    /**
     * 已驳回
     */
    REJECT("25"),
    /**
     * 需重新提交
     */
    RECOMMIT("40"),
    /**
     * 已关闭
     */
    CLOSE("80");

    private String code;

    PatchEnum(String code) {
        this.code = code;
    }

    public static boolean isValue(String status){
        for (PatchEnum value : PatchEnum.values()) {
            if (value.code.equals(status)){
                return true;
            }
        }
        return false;
    }
}
