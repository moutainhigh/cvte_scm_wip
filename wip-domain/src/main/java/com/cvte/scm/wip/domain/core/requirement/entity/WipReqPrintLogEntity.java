package com.cvte.scm.wip.domain.core.requirement.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author jf
 * @since 2020-03-06
 */
@Data
@Accessors(chain = true)
public class WipReqPrintLogEntity {

    private String printLogId;

    @ApiModelProperty(value = "投料单头ID")
    private String headerId;

    @ApiModelProperty(value = "查询字段的JSON字符串")
    private String selectCondition;

    @ApiModelProperty(value = "打印用户")
    private String crtUser;

    @ApiModelProperty(value = "打印时间")
    private Date crtTime;

}