package com.cvte.scm.wip.domain.common.user.entity;

import lombok.Data;

/**
 * 适配五层架构,没有做领域设计改造, comment by xueyuting
 * @Author: wufeng
 * @Date: 2019/10/21 11:43
 */
@Data
public class OrgRelationBaseEntity {
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
