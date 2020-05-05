package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;
import com.cvte.scm.wip.domain.common.user.repository.MultiOrgSwitchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 16:41
 */
@Slf4j
@Service
public class MultiOrgSwitchService {

    @Autowired
    private MultiOrgSwitchRepository multiOrgSwitchRepository;

    public OrgRelationBaseEntity getParentOrgRelationByType(String orgRelationId, String orgType) {
        if(StringUtils.isEmpty(orgRelationId)) {
            throw new ParamsRequiredException("未指定组织节点id");
        }
        return multiOrgSwitchRepository.getParentOrgRelationByType(orgRelationId, orgType);
    }
}
