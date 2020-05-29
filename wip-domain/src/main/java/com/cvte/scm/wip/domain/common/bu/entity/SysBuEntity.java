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
public class SysBuEntity {

    @ApiModelProperty("事业部编码")
    private String bu_code;

    @ApiModelProperty("事业部名称")
    private String bu_name;

}
