package com.cvte.scm.demo.sys.service.base;


import com.cvte.scm.demo.client.sys.base.dto.OrgRelationBaseDTO;
import com.cvte.scm.demo.client.sys.base.dto.SysOrgExt;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 15:44
 */
public interface SysOrgService {

    /**
     * 根据组织关系编码查询组织关系信息
     *
     * @param orgRelationId 组织关系编码
     * @return
     */
    OrgRelationBaseDTO selectOrgRelationById(String orgRelationId);

    /***
     * 根据组织Id获取组织拓展属性
     * @param id
     * @return
     */
    SysOrgExt getOrgExtById(String id);
}
