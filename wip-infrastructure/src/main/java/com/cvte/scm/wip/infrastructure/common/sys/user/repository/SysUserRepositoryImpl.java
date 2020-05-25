package com.cvte.scm.wip.infrastructure.common.sys.user.repository;

import com.cvte.csb.sys.base.entity.SysUser;
import com.cvte.csb.sys.base.mapper.SysUserMapper;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.sys.user.entity.SysUserEntity;
import com.cvte.scm.wip.domain.common.sys.user.repository.SysUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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


    @Override
    public SysUserEntity selectByPrimaryKey(String id) {
        SysUser sysUser = mapper.selectByPrimaryKey(id);
        if (ObjectUtils.isNull(sysUser)) {
            return null;
        }
        return modelMapper.map(sysUser, SysUserEntity.class);
    }
}
