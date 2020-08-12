package com.cvte.scm.wip.domain.core.rework.valueobject.enums;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.enums.Codeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/8/10 10:58
 */
@Getter
@AllArgsConstructor
public enum ReworkTypeEnum implements Codeable {
    PRODUCT_MARK_STICKER("5", "成品唛头和条码更改"),
    PRODUCT_MARK("6", "成品唛头更改"),
    PRODUCT_STICKER("9", "成品只改条码不改唛头"),
    ITEM_MARK_STICKER("7", "物料唛头和标贴更改"),
    ITEM_MARK("8", "物料唛头更改"),
    ;
    private String code, desc;

    public static boolean isItemRework(String reworkType) {
        return StringUtils.isNotBlank(reworkType) && (ReworkTypeEnum.ITEM_MARK.getCode().equals(reworkType) || ReworkTypeEnum.ITEM_MARK_STICKER.getCode().equals(reworkType));
    }
}
