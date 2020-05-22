package com.cvte.scm.wip.domain.core.ckd.dto;

import com.cvte.csb.sys.common.MyBaseEntity;
import com.cvte.csb.validator.vtor.annotation.NotBlankNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-07 11:27
 **/
@Data
@Accessors(chain = true)
public class WipMcTaskLineUpdateDTO extends MyBaseEntity {

    @NotBlankNull(message = "当前操作人不可为空")
    private String optUser;

    private List<UpdateLine> updateList;

    @Data
    @Accessors(chain = true)
    public static class UpdateLine extends MyBaseEntity {

        @NotBlankNull
        @ApiModelProperty(value = "原始单id")
        private String sourceLineId;

        @NotBlankNull
        @ApiModelProperty(value = "配料数量")
        private Integer mcQty;

        @NotBlankNull
        @ApiModelProperty(value = "行状态")
        private String lineStatus;

        @NotBlankNull
        @ApiModelProperty(value = "物料id")
        private String itemId;
    }
}
