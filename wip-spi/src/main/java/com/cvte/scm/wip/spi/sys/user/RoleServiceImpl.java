package com.cvte.scm.wip.spi.sys.user;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.base.constants.CommonConstants;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.context.GlobalContext;
import com.cvte.scm.wip.domain.common.user.entity.RoleEntity;
import com.cvte.scm.wip.domain.common.user.service.RoleService;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.SysRoleApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysRoleDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 09:51
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class RoleServiceImpl implements RoleService {

    private SysRoleApiClient sysRoleApiClient;

    public RoleServiceImpl(SysRoleApiClient sysRoleApiClient) {
        this.sysRoleApiClient = sysRoleApiClient;
    }

    @Override
    public List<RoleEntity> listSysRoleByUserId(String userId) {
        if(StringUtils.isEmpty(userId)) {
            userId = CurrentContext.getCurrentOperatingUser().getId();
        }
        FeignResult<List<SysRoleDTO>> feignResult = sysRoleApiClient.listRoleByUserId(userId);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            List<RoleEntity> roleEntityList = new ArrayList<>();
            for (SysRoleDTO sysRoleDTO : feignResult.getData()) {
                RoleEntity roleEntity = new RoleEntity();
                BeanUtils.copyProperties(sysRoleDTO, roleEntity);
                roleEntityList.add(roleEntity);
            }
            return roleEntityList;
        } else {
            throw new ServerException(DefaultStatusEnum.SERVER_ERROR.getCode(), "远程获取用户角色列表失败, feignResult = " + JSON.toJSONString(feignResult));
        }
    }

    @Override
    public Boolean isAdmin(String userId) {
        List<RoleEntity> sysRoleList = listSysRoleByUserId(userId);
        for(RoleEntity sysRole : sysRoleList) {
            if(CommonConstants.BOOLEAN_TRUE.equals(sysRole.getIsAdmin())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isAdmin() {
        List<RoleEntity> sysRoleList = GlobalContext.getRoleList();
        if(CollectionUtils.isEmpty(sysRoleList)) {
            return isAdmin(CurrentContext.getCurrentOperatingUser().getId());
        }
        for(RoleEntity sysRole : sysRoleList) {
            if(CommonConstants.BOOLEAN_TRUE.equals(sysRole.getIsAdmin())) {
                return true;
            }
        }
        return false;
    }

}
