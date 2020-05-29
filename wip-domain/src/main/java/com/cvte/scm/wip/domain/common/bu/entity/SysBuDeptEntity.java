package com.cvte.scm.wip.domain.common.bu.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/28 12:58
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class SysBuDeptEntity {

    @ApiModelProperty("部门编码")
    private String deptName;

    @ApiModelProperty("部门名称")
    private String deptCode;

    @ApiModelProperty("事业部编码")
    private String buCode;

}
