package com.cvte.scm.wip.domain.core.requirement.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author jf
 * @since 2020-01-01
 */
@Data
@Accessors(chain = true)
public class WipReqLogEntity {


    private String logId;

    private String headerId;

    private String lineId;

    @ApiModelProperty(value = "修改前物料编码")
    private String beforeItemNo;

    @ApiModelProperty(value = "修改后物料编码")
    private String afterItemNo;

    @ApiModelProperty(value = "修改前物料数量")
    private Long beforeItemQty;

    @ApiModelProperty(value = "修改后物料数量")
    private Long afterItemQty;

    @ApiModelProperty(value = "修改前工序号")
    private String beforeWkpNo;

    @ApiModelProperty(value = "修改后工序号")
    private String afterWkpNo;

    @ApiModelProperty(value = "修改前位号")
    private String beforePosNo;

    @ApiModelProperty(value = "修改后位号")
    private String afterPosNo;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    private String updUser;

    private Date updDate;

    @ApiModelProperty(value = "领料批次")
    private String mtrLotNo;

    @ApiModelProperty(value = "领料批次id")
    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;
}