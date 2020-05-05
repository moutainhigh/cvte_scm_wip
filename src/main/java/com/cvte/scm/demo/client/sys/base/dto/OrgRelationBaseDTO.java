package com.cvte.scm.demo.client.sys.base.dto;

import lombok.Data;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 11:43
 */
@Data
public class OrgRelationBaseDTO {
    /**
     * 主键
     */
    private String id;
    /**
     * 组织维度ID
     */
    private String dimensionId;
    /**
     * 组织ID
     */
    private String orgId;
    /**
     * 组织名称
     */
    private String orgName;
    /**
     * 组织英文名称
     */
    private String orgEngName;

    /**
     * 组织类型
     */
    private String orgType;

    /**
     * 汇报关系编码
     */
    private String relationCode;
    /**
     * 父组织ID
     */
    private String parentId;
    /**
     * 是否有权限
     */
    private String isHasPermission;
    /**
     * 排序码
     */
    private Integer sortNo;

    /**
     * 是否启用
     */
    private String isEnabled;

    /**
     * 是否默认组织
     */
    private String isDefaultUnit;
}
