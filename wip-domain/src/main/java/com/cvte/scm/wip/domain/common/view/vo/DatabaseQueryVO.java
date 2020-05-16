package com.cvte.scm.wip.domain.common.view.vo;

import com.cvte.csb.validator.vtor.annotation.NotBlankNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/10 16:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class DatabaseQueryVO {
    @ApiModelProperty("id")
    @NotBlankNull(
            message = "数据源ID不能为空",
            profiles = {"insert", "update"}
    )
    private String id;
    @ApiModelProperty("SQL")
    @NotBlankNull(
            message = "执行SQL不能为空",
            profiles = {"insert", "update"}
    )
    private String sql;
}
