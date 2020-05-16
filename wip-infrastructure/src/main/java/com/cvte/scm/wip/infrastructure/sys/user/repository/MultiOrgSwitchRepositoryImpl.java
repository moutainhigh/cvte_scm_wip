package com.cvte.scm.wip.infrastructure.sys.user.repository;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;
import com.cvte.scm.wip.domain.common.user.repository.MultiOrgSwitchRepository;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.MultiOrgSwitchApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.OrgRelationBaseDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:28
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class MultiOrgSwitchRepositoryImpl implements MultiOrgSwitchRepository {

    @Autowired
    private MultiOrgSwitchApiClient multiOrgSwitchApiClient;

    @Override
    public OrgRelationBaseEntity getParentOrgRelationByType(String orgRelationId, String orgType) {
        FeignResult<OrgRelationBaseDTO> feignResult = multiOrgSwitchApiClient.getParentOrgRelationByType(orgRelationId, orgType);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            OrgRelationBaseEntity orgRelationBaseEntity = new OrgRelationBaseEntity();
            BeanUtils.copyProperties(feignResult.getData(), orgRelationBaseEntity);
            return orgRelationBaseEntity;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程查询组织父节点失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }
}
