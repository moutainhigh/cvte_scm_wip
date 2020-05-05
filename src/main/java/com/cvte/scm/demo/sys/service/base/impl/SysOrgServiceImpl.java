package com.cvte.scm.demo.sys.service.base.impl;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.scm.demo.client.common.dto.FeignResult;
import com.cvte.scm.demo.client.sys.base.SysOrgApiClient;
import com.cvte.scm.demo.client.sys.base.dto.OrgRelationBaseDTO;
import com.cvte.scm.demo.client.sys.base.dto.SysOrgExt;
import com.cvte.scm.demo.sys.service.base.SysOrgService;
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
public class SysOrgServiceImpl implements SysOrgService {

    @Autowired
    private SysOrgApiClient sysOrgApiClient;

    @Override
    public OrgRelationBaseDTO selectOrgRelationById(String orgRelationId) {
        if(StringUtils.isEmpty(orgRelationId)) {
            throw new ParamsRequiredException("组织关系id为空");
        }
        FeignResult<OrgRelationBaseDTO> feignResult = sysOrgApiClient.getOrgRelationById(orgRelationId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程查询组织关系失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public SysOrgExt getOrgExtById(String id) {
        if(StringUtils.isEmpty(id)) {
            throw new ParamsRequiredException("组织id为空");
        }
        FeignResult<SysOrgExt> feignResult = sysOrgApiClient.getOrgExtById(id);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程查询组织拓展信息失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }
}
