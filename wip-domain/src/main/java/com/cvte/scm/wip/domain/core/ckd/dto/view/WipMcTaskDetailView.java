package com.cvte.scm.wip.domain.core.ckd.dto.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author zy
 * @date 2020-04-30 09:49
 **/
@Data
@Accessors(chain = true)
public class WipMcTaskDetailView {

    @ApiModelProperty(value = "主键")
    private String mcTaskId;

    @ApiModelProperty(value = "配料任务编号")
    private String mcTaskNo;

    @ApiModelProperty(value = "配料任务编号")
    private String mcWfId;

    @ApiModelProperty(value = "工厂")
    private String factoryId;

    @ApiModelProperty(value = "ebs组织Id")
    private String organizationId;

    @ApiModelProperty(value = "组织id")
    private String orgId;

    @ApiModelProperty(value = "客户")
    private String client;

    @ApiModelProperty(value = "齐套日期")
    private Date mtrReadyTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "当前状态")
    private String status;

    @ApiModelProperty(value = "内勤")
    private String backOffice;

    @ApiModelProperty(value = "事业部")
    private String buName;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;

    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;

    @ApiModelProperty(value = "${field.comment}")
    private String crtHost;

    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;

    @ApiModelProperty(value = "${field.comment}")
    private String updUser;

    @ApiModelProperty(value = "${field.comment}")
    private String updHost;

    @ApiModelProperty(value = "配料任务行")
    private List<WipMcTaskLineView> childs;


}
