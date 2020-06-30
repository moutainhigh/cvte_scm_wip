package com.cvte.scm.wip.infrastructure.common.sys.user.repository;

import com.cvte.csb.base.enums.YesOrNoEnum;
import com.cvte.csb.sys.base.entity.SysRole;
import com.cvte.csb.sys.base.entity.SysRoleUserUnit;
import com.cvte.csb.sys.base.entity.SysUser;
import com.cvte.csb.sys.base.enums.RoleRelTypeEnum;
import com.cvte.csb.sys.base.mapper.SysRoleMapper;
import com.cvte.csb.sys.base.mapper.SysRoleUserUnitMapper;
import com.cvte.csb.sys.base.mapper.SysUserMapper;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.sys.user.entity.SysUserEntity;
import com.cvte.scm.wip.domain.common.sys.user.repository.SysUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zy
 * @date 2020-05-25 09:44
 **/
@Repository
public class SysUserRepositoryImpl implements SysUserRepository {

    @Autowired
    private SysUserMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SysRoleUserUnitMapper sysRoleUserUnitMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public SysUserEntity selectByPrimaryKey(String id) {
        SysUser sysUser = mapper.selectByPrimaryKey(id);
        if (ObjectUtils.isNull(sysUser)) {
            return null;
        }
        return modelMapper.map(sysUser, SysUserEntity.class);
    }

    public List<String> listRoleUserIds(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            return new ArrayList<>();
        }

        SysRole sysRoleQuery = new SysRole();
        sysRoleQuery.setRoleCode(roleCode);
        sysRoleQuery.setIsDeleted(YesOrNoEnum.NO.getValue());
        sysRoleQuery.setIsEnabled(YesOrNoEnum.YES.getValue());
        SysRole sysRole = sysRoleMapper.selectOne(sysRoleQuery);
        if (ObjectUtils.isNull(sysRole)) {
            return new ArrayList<>();
        }

        SysRoleUserUnit sysRoleUserUnitQuery = new SysRoleUserUnit();
        sysRoleUserUnitQuery.setRoleId(sysRole.getId());
        sysRoleUserUnitQuery.setRoleRelType(RoleRelTypeEnum.USER.getValue());
        sysRoleUserUnitQuery.setIsEnabled(YesOrNoEnum.YES.getValue());
        List<SysRoleUserUnit> currentSysRoleUserUnits = sysRoleUserUnitMapper.select(sysRoleUserUnitQuery);
        return new ArrayList<>(currentSysRoleUserUnits.stream().map(SysRoleUserUnit::getRoleRelId).collect(Collectors.toSet()));
    }

}
