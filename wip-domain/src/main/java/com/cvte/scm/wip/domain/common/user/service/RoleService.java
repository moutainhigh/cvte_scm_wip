package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.csb.base.constants.CommonConstants;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.context.GlobalContext;
import com.cvte.scm.wip.domain.common.user.entity.RoleEntity;
import com.cvte.scm.wip.domain.common.user.repository.RoleRepository;
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
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleEntity> listSysRoleByUserId(String userId) {
        if(StringUtils.isEmpty(userId)) {
            userId = CurrentContext.getCurrentOperatingUser().getId();
        }
        return roleRepository.listRoleByUserId(userId);
    }

    public Boolean isAdmin(String userId) {
        List<RoleEntity> sysRoleList = listSysRoleByUserId(userId);
        for(RoleEntity sysRole : sysRoleList) {
            if(CommonConstants.BOOLEAN_TRUE.equals(sysRole.getIsAdmin())) {
                return true;
            }
        }
        return false;
    }

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
