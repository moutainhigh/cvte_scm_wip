package com.cvte.scm.demo.client.sys.base.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @Author: wufeng
 * @Date: 2019/11/9 13:21
 */
@Data
public class SysPost {

    @ApiModelProperty(value = "岗位Id", hidden = true)
    private String id;

    @ApiModelProperty(hidden = true)
    private String appId;

    @ApiModelProperty(hidden = true)
    private String systemId;

    @ApiModelProperty(value = "父节点ID")
    private String parentId;

    @ApiModelProperty(value = "岗位编码", hidden = true)
    private String postCode;

    @ApiModelProperty(value = "岗位名称", example = "开发工程师")
    private String postName;

    @ApiModelProperty(value = "岗位简称", example = "abbr")
    private String abbrName;

    @ApiModelProperty(hidden = true)
    private String postType;

    @ApiModelProperty(value = "生效日期")
    private Date effectiveDateBegin;

    @ApiModelProperty(hidden = true)
    private Date effectiveDateEnd;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否启用", allowableValues = "0,1", example = "1")
    private String isEnabled;

    @ApiModelProperty(hidden = true)
    private String isDeleted;

    @ApiModelProperty(value = "序号")
    private Long sortNo;

    @ApiModelProperty(hidden = true)
    private String attribute1;

    @ApiModelProperty(hidden = true)
    private String attribute2;

    @ApiModelProperty(hidden = true)
    private String attribute3;

    @ApiModelProperty(hidden = true)
    private String crtUser;

    @ApiModelProperty(hidden = true)
    private String crtName;

    @ApiModelProperty(hidden = true)
    private String crtHost;

    @ApiModelProperty(hidden = true)
    private Date crtTime;

    @ApiModelProperty(hidden = true)
    private String updUser;

    @ApiModelProperty(hidden = true)
    private String updName;

    @ApiModelProperty(hidden = true)
    private String updHost;

    @ApiModelProperty(hidden = true)
    private Date updTime;

    @ApiModelProperty(hidden = true)
    private BigDecimal version;

    @ApiModelProperty(value = "成立日期")
    private Date foundDate;

    @ApiModelProperty(value = "是否虚拟岗位", example = "0")
    private String isVirtualPost;
}
