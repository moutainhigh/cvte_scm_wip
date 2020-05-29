package com.cvte.scm.wip.domain.core.ckd.dto.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-05-12 12:01
 **/
@Data
@Accessors(chain = true)
public class McTaskDeliveringStockView {

    private String inoutStockLineId;

    private String inoutStockId;

    private String mcTaskLineId;

    private String mcTaskId;

    @ApiModelProperty(value = "行状态")
    private String lineStatus;

    @ApiModelProperty(value = "调拨单行号")
    private String deliveryLineNo;

    @ApiModelProperty(value = "调拨单行id")
    private String deliveryLineId;

    @ApiModelProperty(value = "调拨单头号")
    private String deliveryNo;

    @ApiModelProperty(value = "调拨单头id")
    private String deliveryId;
}
