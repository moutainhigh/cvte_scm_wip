package com.cvte.scm.wip.infrastructure.user.repository;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.scm.wip.domain.common.user.entity.OrgExtEntity;
import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;
import com.cvte.scm.wip.domain.common.user.repository.OrgRepository;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.SysOrgApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.OrgRelationBaseDTO;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysOrgExt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class OrgRepositoryImpl implements OrgRepository {

    @Autowired
    private SysOrgApiClient sysOrgApiClient;

    @Override
    public OrgRelationBaseEntity getOrgRelationById(String orgRelationId) {
        FeignResult<OrgRelationBaseDTO> feignResult = sysOrgApiClient.getOrgRelationById(orgRelationId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            OrgRelationBaseEntity orgRelationBaseEntity = new OrgRelationBaseEntity();
            BeanUtils.copyProperties(feignResult.getData(), orgRelationBaseEntity);
            return orgRelationBaseEntity;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程查询组织关系失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public OrgExtEntity getOrgExtById(String id) {
        FeignResult<SysOrgExt> feignResult = sysOrgApiClient.getOrgExtById(id);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            OrgExtEntity orgExtEntity = new OrgExtEntity();
            BeanUtils.copyProperties(feignResult.getData(), orgExtEntity);
            return orgExtEntity;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程查询组织拓展信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }
}
