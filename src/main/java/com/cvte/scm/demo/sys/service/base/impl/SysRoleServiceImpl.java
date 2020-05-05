package com.cvte.scm.demo.sys.service.base.impl;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.base.constants.CommonConstants;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.demo.client.common.dto.FeignResult;
import com.cvte.scm.demo.client.sys.base.SysRoleApiClient;
import com.cvte.scm.demo.client.sys.base.dto.SysRoleDTO;
import com.cvte.scm.demo.sys.context.GlobalContext;
import com.cvte.scm.demo.sys.service.base.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 09:43
 */
@Slf4j
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleApiClient sysRoleApiClient;

    @Override
    public List<SysRoleDTO> listSysRoleByUserId(String userId) {
        if(StringUtils.isEmpty(userId)) {
            userId = CurrentContext.getCurrentOperatingUser().getId();
        }
        FeignResult<List<SysRoleDTO>> feignResult = sysRoleApiClient.listRoleByUserId(userId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            return feignResult.getData();
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户角色列表失败, feignResult = " + JSON.toJSONString(feignResult));
        }

    }

    @Override
    public Boolean isAdmin(String userId) {
        List<SysRoleDTO> sysRoleList = listSysRoleByUserId(userId);
        for(SysRoleDTO sysRole : sysRoleList) {
            if(CommonConstants.BOOLEAN_TRUE.equals(sysRole.getIsAdmin())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isAdmin() {
        List<SysRoleDTO> sysRoleList = GlobalContext.getRoleList();
        if(CollectionUtils.isEmpty(sysRoleList)) {
            return isAdmin(CurrentContext.getCurrentOperatingUser().getId());
        }
        for(SysRoleDTO sysRole : sysRoleList) {
            if(CommonConstants.BOOLEAN_TRUE.equals(sysRole.getIsAdmin())) {
                return true;
            }
        }
        return false;
    }


}
