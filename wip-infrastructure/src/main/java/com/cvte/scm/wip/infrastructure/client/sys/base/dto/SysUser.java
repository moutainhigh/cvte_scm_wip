package com.cvte.scm.wip.infrastructure.client.sys.base.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @Author: wufeng
 * @Date: 2019/11/9 13:07
 */
@Data
public class SysUser {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("账号名")
    private String account;

    @ApiModelProperty("域密码")
    private String domainPwd;

    @ApiModelProperty("独立密码")
    private String independentPwd;

    @ApiModelProperty("账号类型")
    private String accountType;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("生日")
    private Date birthday;

    @ApiModelProperty("手机")
    private String telephone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("岗位ID")
    private String postId;

    @ApiModelProperty("是否锁定")
    private String isLock;

    @ApiModelProperty("锁定原因")
    private String lockReason;

    @ApiModelProperty("密码最后修改时间")
    private Date pwdLastUpdTime;

    @ApiModelProperty("是否有效")
    private String isEnabled;

    @ApiModelProperty("是否二次验证")
    private String isSecondVerify;

    @ApiModelProperty("二次验证类型")
    private String secondVerifyType;

    @ApiModelProperty("弹性域1")
    private String attribute1;

    @ApiModelProperty("弹性域2")
    private String attribute2;

    @ApiModelProperty("弹性域3")
    private String attribute3;

    @ApiModelProperty("弹性域4")
    private String attribute4;

    @ApiModelProperty("弹性域5")
    private String attribute5;

    @ApiModelProperty("弹性域6")
    private String attribute6;

    @ApiModelProperty("创建人ID")
    private String crtUser;

    @ApiModelProperty("创建人姓名")
    private String crtName;

    @ApiModelProperty("创建主机")
    private String crtHost;

    @ApiModelProperty("创建时间")
    private Date crtTime;

    @ApiModelProperty("更新人ID")
    private String updUser;

    @ApiModelProperty("更新人姓名")
    private String updName;

    @ApiModelProperty("更新主机")
    private String updHost;

    @ApiModelProperty("更新时间")
    private Date updTime;

    @ApiModelProperty("版本")
    private BigDecimal version;

    @ApiModelProperty("登录方式")
    private String loginType;
}
