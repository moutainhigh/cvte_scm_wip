package com.cvte.scm.demo.sys.service.base.impl;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.demo.client.common.dto.FeignResult;
import com.cvte.scm.demo.client.sys.base.MultiOrgSwitchApiClient;
import com.cvte.scm.demo.client.sys.base.dto.OrgRelationBaseDTO;
import com.cvte.scm.demo.sys.service.base.SysMultiOrgSwitchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 16:41
 */
@Slf4j
@Service
public class SysMultiOrgSwitchServiceImpl implements SysMultiOrgSwitchService {

    @Autowired
    private MultiOrgSwitchApiClient multiOrgSwitchApiClient;


    @Override
    public OrgRelationBaseDTO getParentOrgRelationByType(String orgRelationId, String orgType) {
        if(StringUtils.isEmpty(orgRelationId)) {
            throw new ParamsRequiredException("未指定组织节点id");
        }
        FeignResult<OrgRelationBaseDTO> feignResult = multiOrgSwitchApiClient.getParentOrgRelationByType(orgRelationId, orgType);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程查询组织父节点失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }
}
