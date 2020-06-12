package com.cvte.scm.wip.spi.sys.user;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.user.entity.OrgExtEntity;
import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.OrgService;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.SysOrgApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.OrgRelationBaseDTO;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysOrgExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 09:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service(value = "wipOrgService")
public class OrgServiceImpl implements OrgService {

    private SysOrgApiClient sysOrgApiClient;

    public OrgServiceImpl(SysOrgApiClient sysOrgApiClient) {
        this.sysOrgApiClient = sysOrgApiClient;
    }

    @Override
    public OrgRelationBaseEntity selectOrgRelationById(String orgRelationId) {
        if(StringUtils.isEmpty(orgRelationId)) {
            throw new ParamsRequiredException("组织关系id为空");
        }
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
        if(StringUtils.isEmpty(id)) {
            throw new ParamsRequiredException("组织id为空");
        }
        FeignResult<SysOrgExt> feignResult = sysOrgApiClient.getOrgExtById(id);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            if (ObjectUtils.isNull(feignResult.getData())) {
                return null;
            }

            OrgExtEntity orgExtEntity = new OrgExtEntity();
            BeanUtils.copyProperties(feignResult.getData(), orgExtEntity);
            return orgExtEntity;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程查询组织拓展信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

}
