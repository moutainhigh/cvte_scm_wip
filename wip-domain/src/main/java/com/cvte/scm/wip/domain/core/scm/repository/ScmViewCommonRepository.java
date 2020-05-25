package com.cvte.scm.wip.domain.core.scm.repository;

import com.cvte.scm.wip.domain.core.scm.dto.query.SysOrgOrganizationVQuery;
import com.cvte.scm.wip.domain.core.scm.vo.SysOrgOrganizationVO;

import java.util.List;

public interface ScmViewCommonRepository {

    List<SysOrgOrganizationVO> listSysOrgOrganizationVO(SysOrgOrganizationVQuery query);

    String getFactoryCodeById(String factoryId);
}
