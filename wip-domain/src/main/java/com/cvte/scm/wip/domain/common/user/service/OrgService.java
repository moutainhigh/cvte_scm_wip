package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.scm.wip.domain.common.user.entity.OrgExtEntity;
import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;
import com.cvte.scm.wip.domain.common.user.repository.OrgRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Author: wufeng
 * @Date: 2019/10/21 15:46
 */
@Slf4j
@Service
public class OrgService {

    @Autowired
    private OrgRepository orgRepository;

    public OrgRelationBaseEntity selectOrgRelationById(String orgRelationId) {
        if(StringUtils.isEmpty(orgRelationId)) {
            throw new ParamsRequiredException("组织关系id为空");
        }
        return orgRepository.getOrgRelationById(orgRelationId);
    }

    public OrgExtEntity getOrgExtById(String id) {
        if(StringUtils.isEmpty(id)) {
            throw new ParamsRequiredException("组织id为空");
        }
        return orgRepository.getOrgExtById(id);
    }
}
