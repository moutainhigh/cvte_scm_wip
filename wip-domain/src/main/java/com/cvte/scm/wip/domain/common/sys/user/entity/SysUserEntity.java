package com.cvte.scm.wip.domain.common.sys.user.entity;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zy
 * @date 2020-05-25 09:50
 **/
@Data
@Accessors(chain = true)
public class SysUserEntity extends BaseModel {

    private String id;

    private String account;

    private String independentPwd;

    private String accountType;
    private String name;

    private String sex;

    private Date birthday;

    private String telephone;

    private String email;

    private String postId;

    private String isLock;

    private String lockReason;

    private Date pwdLastUpdTime;

    private String isEnabled;

    private String isSecondVerify;

    private String secondVerifyType;

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private String attribute5;

    private String attribute6;

    private String crtUser;

    private String crtName;

    private String crtHost;

    private Date crtTime;

    private String updUser;

    private String updName;

    private String updHost;

    private Date updTime;

    private BigDecimal version;

    private String loginType;

}
