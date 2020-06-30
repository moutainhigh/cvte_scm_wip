package com.cvte.scm.wip.domain.core.ckd.dto.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-04-29 16:04
 **/
@Getter
@Setter
@Accessors(chain = true)
public class WipMcTaskCompareView {

    @ApiModelProperty(value = "物料id")
    private String itemId;

    @ApiModelProperty(value = "物料编码")
    private String itemCode;

    @ApiModelProperty(value = "数量")
    private Integer qty;

    @ApiModelProperty(value = "变更类型编码")
    private String changTypeCode;

    @ApiModelProperty(value = "变更类型名")
    private String changTypeName;

    @ApiModelProperty(value = "变更数量")
    private Integer changQty;
}
