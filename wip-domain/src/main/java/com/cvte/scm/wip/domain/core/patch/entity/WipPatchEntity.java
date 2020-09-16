package com.cvte.scm.wip.domain.core.patch.entity;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @descriptions: 补料单头表
 * @author: ykccchen
 * @date: 2020/7/24 11:00 上午
 */
@Data
@Table(name = "wip_patch")
@Accessors(chain = true)
public class WipPatchEntity extends BaseModel {

    @ApiModelProperty("补料单ID")
    @Id
    private String billId;

    @ApiModelProperty("组织ID")
    private String organizationId;

    @ApiModelProperty("工厂ID")
    private String factoryId;

    @ApiModelProperty("工单ID")
    private String moId;

    @ApiModelProperty("生产批次号")
    private String moLotNo;

    @ApiModelProperty("物料ID")
    private String itemId;

    @ApiModelProperty("补料单类型")
    private String billType;

    @ApiModelProperty("状态表主键ID值")
    private Integer wfId;

    @ApiModelProperty("补料原因")
    private String purExplain;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("齐套时间")
    private Date mtrReadyDate;

    @ApiModelProperty("投料单头ID")
    private String headerId;

    @ApiModelProperty("备注")
    private String rmk;

    @ApiModelProperty("行信息")
    private List<WipPatchLinesEntity> linesList = new ArrayList<>();




}
