package com.cvte.scm.wip.infrastructure.scm.repository;

import com.cvte.scm.wip.domain.core.scm.dto.query.SysOrgOrganizationVQuery;
import com.cvte.scm.wip.domain.core.scm.repository.ScmViewCommonRepository;
import com.cvte.scm.wip.domain.core.scm.vo.SysOrgOrganizationVO;
import com.cvte.scm.wip.infrastructure.scm.mapper.ScmViewCommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScmViewCommonRepositoryImpl implements ScmViewCommonRepository {

    @Autowired
    private ScmViewCommonMapper scmViewCommonMapper;

    @Override
    public List<SysOrgOrganizationVO> listSysOrgOrganizationVO(SysOrgOrganizationVQuery query) {
        return scmViewCommonMapper.listSysOrgOrganizationVO(query);
    }

    @Override
    public String getFactoryCodeById(String factoryId) {
        return scmViewCommonMapper.getFactoryCodeById(factoryId);
    }
}