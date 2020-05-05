package com.cvte.scm.demo.sys.service.base;


import com.cvte.scm.demo.client.sys.base.dto.OrgRelationBaseDTO;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 16:40
 */
public interface SysMultiOrgSwitchService {

    /**
     *
     * @param orgRelationId
     * @param orgType
     * @return
     */
    OrgRelationBaseDTO getParentOrgRelationByType(String orgRelationId, String orgType);
}
