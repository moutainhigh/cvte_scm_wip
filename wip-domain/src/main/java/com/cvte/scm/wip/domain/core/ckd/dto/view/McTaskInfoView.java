package com.cvte.scm.wip.domain.core.ckd.dto.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zy
 * @date 2020-04-30 15:11
 **/
@Data
public class McTaskInfoView {

    private String mcTaskNo;

    private String mcTaskId;

    private String client;

    private String status;

    private String statusName;

    private Date mtrReadyTime;

    private String factoryName;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

    private String remark;

    @ApiModelProperty("内勤")
    private String backOffice;

    @ApiModelProperty("配料完成状态")
    private String mcFinishStatus;

    @ApiModelProperty("配料完成状态名")
    private String mcFinishStatusName;

    @ApiModelProperty("生管审核人")
    private String pmAuditUser;

    @ApiModelProperty("生管审核时间")
    private Date pmAuditDate;

    @ApiModelProperty("工厂确认人")
    private String factoryConfirmUser;

    @ApiModelProperty("工厂确认时间")
    private String factoryConfirmDate;

    private String versionId;

    private String versionNo;

    private Date versionDate;

    private String buName;

    private String deptName;

    private String orgId;

    private String ebsOrganizationCode;

    private String deliveryInStatus;

    private String deliveryOutStatus;

    private String inoutStockId;

}
