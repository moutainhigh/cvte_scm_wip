package com.cvte.scm.wip.domain.core.ckd.dto.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zy
 * @date 2020-04-29 16:38
 **/
@Data
@Accessors(chain = true)
public class WipMcTaskLineVersionView {

    @ApiModelProperty(value = "版本行id")
    private String id;

    @ApiModelProperty(value = "版本id")
    private String versionId;

    @ApiModelProperty(value = "版本日期")
    private Date versionDate;

    @ApiModelProperty(value = "物料id")
    private String itemId;

    @ApiModelProperty(value = "物料编码")
    private String itemCode;

    @ApiModelProperty(value = "销售订单行号")
    private String soLineNo;

    @ApiModelProperty(value = "销售订单行id")
    private String soLineId;

    @ApiModelProperty(value = "数量")
    private Integer qty;

    private Date crtTime;

    private String crtUser;

    private String crtHost;

}
