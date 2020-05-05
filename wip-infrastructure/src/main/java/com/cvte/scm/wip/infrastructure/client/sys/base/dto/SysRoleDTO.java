package com.cvte.scm.wip.infrastructure.client.sys.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 09:30
 */
@Data
public class SysRoleDTO implements Serializable {
    /**
     * id
     */
    private String id;
    /**
     * 租户ID
     */
    private String appId;
    /**
     * 业务系统
     */
    private String systemId;
    /**
     * 父角色ID
     */
    private String parentId;
    /**
     * 角色编码
     */
    private String roleCode;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色简称
     */
    private String roleAbbrName;
    /**
     * 角色类型
     */
    private String roleType;
    /**
     * 生效日期
     */
    private Date effectiveDateBegin;
    /**
     * 失效日期
     */
    private Date effectiveDateEnd;
    /**
     * 是否超级管理员
     */
    private String isAdmin;
    /**
     * 是否系统内置
     */
    private String isSystem;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否有效
     */
    private String isEnabled;
    /**
     * 排序码
     */
    private Long sortNo;
    /**
     * 角色来源
     */
    private String roleSource;

}
