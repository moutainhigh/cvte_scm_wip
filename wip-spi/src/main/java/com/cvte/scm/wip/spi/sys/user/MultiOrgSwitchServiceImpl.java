package com.cvte.scm.wip.spi.sys.user;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.user.entity.OrgRelationBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.MultiOrgSwitchService;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.MultiOrgSwitchApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.OrgRelationBaseDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 09:30
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service(value = "wipMultiOrgSwitchService")
public class MultiOrgSwitchServiceImpl implements MultiOrgSwitchService {

    private MultiOrgSwitchApiClient multiOrgSwitchApiClient;

    public MultiOrgSwitchServiceImpl(MultiOrgSwitchApiClient multiOrgSwitchApiClient) {
        this.multiOrgSwitchApiClient = multiOrgSwitchApiClient;
    }

    @Override
    public OrgRelationBaseEntity getParentOrgRelationByType(String orgRelationId, String orgType) {
        if(StringUtils.isEmpty(orgRelationId)) {
            throw new ParamsRequiredException("未指定组织节点id");
        }
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
