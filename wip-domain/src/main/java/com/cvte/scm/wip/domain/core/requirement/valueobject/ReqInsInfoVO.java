package com.cvte.scm.wip.domain.core.requirement.valueobject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/3 19:55
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ReqInsInfoVO {

    @ApiModelProperty("指令集ID")
    private String insHeaderId;

    @ApiModelProperty("目标投料单ID")
    private String aimHeaderId;

    @ApiModelProperty("勾选框标识")
    private Integer headFlag;

    @ApiModelProperty("更改单号")
    private String billNo;

    @ApiModelProperty("组织ID")
    private String organizationId;

    @ApiModelProperty("更改单类型")
    private String billType;

    @ApiModelProperty("目标批次号")
    private String aimReqLotNo;

    @ApiModelProperty("生效时间")
    private Date enableDate;

    @ApiModelProperty("确认人")
    private String confirmedBy;

    @ApiModelProperty("指令集状态")
    private String status;

    @ApiModelProperty("更新时间")
    private Date updTime;

    @ApiModelProperty("变更类型")
    private String operationType;

    @ApiModelProperty("工序")
    private String wkpNo;

    @ApiModelProperty("批次")
    private String moLotNo;

    @ApiModelProperty("位号")
    private String posNo;

    @ApiModelProperty("改前物料ID")
    private String itemIdOld;

    @ApiModelProperty("改前物料编码")
    private String itemNoOld;

    @ApiModelProperty("改前物料描述")
    private String itemSpecOld;

    @ApiModelProperty("改后物料ID")
    private String itemIdNew;

    @ApiModelProperty("改后物料编码")
    private String itemNoNew;

    @ApiModelProperty("改后物料描述")
    private String itemSpecNew;

}
