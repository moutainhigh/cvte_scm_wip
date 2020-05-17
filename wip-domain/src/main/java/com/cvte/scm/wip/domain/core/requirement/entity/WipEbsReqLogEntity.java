package com.cvte.scm.wip.domain.core.requirement.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * EBS投料单的操作记录
 *
 * @author author
 * @since 2020-03-12
 */
@Data
@Accessors(chain = true)
public class WipEbsReqLogEntity {

    private String logId;

    @ApiModelProperty(value = "操作的表")
    private String opTable;

    @ApiModelProperty(value = "操作类型")
    private String opType;

    @ApiModelProperty(value = "${field.comment}")
    private String opPos;

    @ApiModelProperty(value = "${field.comment}")
    private String opTs;

    @ApiModelProperty(value = "请求时间")
    private String requestTs;

    @ApiModelProperty(value = "库存组织")
    private Integer organizationId;

    @ApiModelProperty(value = "对应投料单header_id")
    private Integer wipEntityId;

    @ApiModelProperty(value = "工序")
    private Integer operationSeqNum;

    @ApiModelProperty(value = "改前物料ID")
    private Integer beforeItemId;

    @ApiModelProperty(value = "改后物料ID")
    private Integer afterItemId;

    @ApiModelProperty(value = "改前投料详情")
    private String before;

    @ApiModelProperty(value = "改后投料详情")
    private String after;

    @ApiModelProperty(value = "处理状态")
    private String processStatus;

    @ApiModelProperty(value = "成功处理后的分组标识")
    private String groupId;

    @ApiModelProperty(value = "异常原因")
    private String exceptionReason;

    @ApiModelProperty(value = "创建人")
    private String crtUser;

    @ApiModelProperty(value = "创建时间")
    private Date crtDate;

    @ApiModelProperty(value = "更新人")
    private String updUser;

    @ApiModelProperty(value = "更新时间")
    private Date updDate;

}
