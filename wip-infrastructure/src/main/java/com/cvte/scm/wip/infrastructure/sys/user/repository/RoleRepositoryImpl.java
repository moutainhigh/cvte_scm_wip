package com.cvte.scm.wip.infrastructure.sys.user.repository;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.scm.wip.domain.common.user.entity.RoleEntity;
import com.cvte.scm.wip.domain.common.user.repository.RoleRepository;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.SysRoleApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysRoleDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:42
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    @Autowired
    private SysRoleApiClient sysRoleApiClient;

    @Override
    public List<RoleEntity> listRoleByUserId(String userId) {
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
}
