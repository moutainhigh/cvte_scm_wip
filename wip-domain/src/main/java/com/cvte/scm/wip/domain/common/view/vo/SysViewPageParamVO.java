package com.cvte.scm.wip.domain.common.view.vo;

import com.cvte.csb.base.enums.OrderTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import jodd.vtor.constraint.NotBlank;
import jodd.vtor.constraint.NotNull;
import lombok.Data;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/10 16:52
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class SysViewPageParamVO {
    @ApiModelProperty(
            value = "数据源编码",
            required = true
    )
    @NotNull(
            message = "数据源编码不能为空",
            profiles = {"7"}
    )
    @NotBlank(
            message = "数据源编码不能为空",
            profiles = {"7"}
    )
    private String viewDatasourceId;
    @ApiModelProperty(
            value = "视图编码",
            required = true
    )
    @NotNull(
            message = "视图编码不能为空",
            profiles = {"7"}
    )
    @NotBlank(
            message = "视图编码不能为空",
            profiles = {"7"}
    )
    private String viewId;
    @ApiModelProperty("高级查询条件")
    private String advConditions;
    @ApiModelProperty(
            value = "是否自定义视图",
            required = true,
            allowableValues = "0, 1"
    )
    @NotNull(
            message = "是否自定义视图不能为空",
            profiles = {"7"}
    )
    @NotBlank(
            message = "是否自定义视图不能为空",
            profiles = {"7"}
    )
    private String isCustomerView;
    @ApiModelProperty(
            value = "当前页",
            required = true
    )
    private Integer pageNum;
    @ApiModelProperty("每页记录数")
    private Integer pageSize;
    @ApiModelProperty("排序字段")
    private String orderBy;
    @ApiModelProperty("排序类型")
    private OrderTypeEnum order;
}
