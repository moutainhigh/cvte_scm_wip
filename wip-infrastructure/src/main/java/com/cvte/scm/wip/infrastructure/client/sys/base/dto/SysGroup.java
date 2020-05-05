package com.cvte.scm.wip.infrastructure.client.sys.base.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @Author: wufeng
 * @Date: 2019/11/9 13:29
 */
@Data
public class SysGroup {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("租户ID")
    private String appId;

    @ApiModelProperty("系统ID")
    private String systemId;

    @ApiModelProperty("上级ID")
    private String parentId;

    @ApiModelProperty("群组编码")
    private String groupCode;

    @ApiModelProperty("群组名称")
    private String groupName;

    @ApiModelProperty("群组简称")
    private String groupAbbrName;

    @ApiModelProperty("群组类型")
    private String groupType;

    @ApiModelProperty("生效日期")
    private Date effectiveDateBegin;

    @ApiModelProperty("失效日期")
    private Date effectiveDateEnd;

    @ApiModelProperty("数据源类型")
    private String dsType;

    @ApiModelProperty("数据源配置（SQL/接口地址）")
    private String dsConfig;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("启用邮件组")
    private String enableEmailGroup;

    @ApiModelProperty("邮件后缀")
    private String emailSuffix;

    @ApiModelProperty("邮件负责人")
    private String emailPrincipal;

    @ApiModelProperty("是否有效")
    private String isEnabled;

    @ApiModelProperty("是否删除")
    private String isDeleted;

    @Column(name = "sort_no")
    private Long sortNo;

    @Column(name = "attribute1")
    private String attribute1;

    @Column(name = "attribute2")
    private String attribute2;

    @Column(name = "attribute3")
    private String attribute3;

    @Column(name = "crt_user")
    private String crtUser;

    @Column(name = "crt_name")
    private String crtName;

    @Column(name = "crt_host")
    private String crtHost;

    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_user")
    private String updUser;

    @Column(name = "upd_name")
    private String updName;

    @Column(name = "upd_host")
    private String updHost;

    @Column(name = "upd_time")
    private Date updTime;

    @Column(name = "version")
    private BigDecimal version;

    private Integer total;
}
