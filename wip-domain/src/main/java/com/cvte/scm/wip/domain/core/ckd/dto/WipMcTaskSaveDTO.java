package com.cvte.scm.wip.domain.core.ckd.dto;


import com.cvte.csb.sys.common.MyBaseEntity;
import com.cvte.csb.validator.vtor.annotation.NotBlankNull;
import io.swagger.annotations.ApiModelProperty;
import jodd.vtor.constraint.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author zy
 * @date 2020-04-28 10:31
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WipMcTaskSaveDTO extends MyBaseEntity {

    @NotBlankNull
    @ApiModelProperty(value = "开立类型")
    private String optType;

    @NotBlankNull
    @ApiModelProperty(value = "当前操作人")
    private String optUser;

    @ApiModelProperty(value = "内勤账号")
    private String backOffice;

    private List<McReq> mcReqs;

    private List<McLine> mcLines;


    @Data
    @Accessors(chain = true)
    public static class McReq extends MyBaseEntity {

        @NotBlankNull
        @ApiModelProperty(value = "原始id")
        private String mcReqId;

        @NotBlankNull
        @ApiModelProperty(value = "工厂")
        private String factoryId;

        @NotBlankNull
        @ApiModelProperty(value = "客户")
        private String client;

        @NotNull
        @ApiModelProperty(value = "齐套日期")
        private Date mtrReadyTime;

        @ApiModelProperty(value = "配料任务id(用于合并开立场景，单独开立为空)")
        private String mcTaskId;

        @NotBlankNull
        @ApiModelProperty(value = "事业部")
        private String buName;

        @NotBlankNull
        @ApiModelProperty(value = "部门")
        private String deptName;

        @NotBlankNull
        @ApiModelProperty(value = "ebs库存组织id")
        private String organizationId;
    }

    @Data
    @Accessors(chain = true)
    public static class McLine extends MyBaseEntity {

        @NotBlankNull
        @ApiModelProperty(value = "原始单行号")
        private String sourceLineNo;

        @NotBlankNull
        @ApiModelProperty(value = "原始单id")
        private String sourceLineId;

        @NotBlankNull
        @ApiModelProperty(value = "物料id")
        private String itemId;

        @NotBlankNull
        @ApiModelProperty(value = "配料数量")
        private Integer mcQty;
    }


}
