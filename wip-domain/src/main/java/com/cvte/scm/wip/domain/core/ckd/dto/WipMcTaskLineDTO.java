package com.cvte.scm.wip.domain.core.ckd.dto;

import com.cvte.csb.sys.common.MyBaseEntity;
import com.cvte.csb.validator.vtor.annotation.NotBlankNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-05-07 10:37
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WipMcTaskLineDTO extends MyBaseEntity {

    @NotBlankNull
    @ApiModelProperty(value = "原始单id")
    private String sourceLineId;

    @NotBlankNull
    @ApiModelProperty(value = "配料数量")
    private Integer mcQty;
}
