package com.cvte.scm.wip.infrastructure.scm.mapper;

import com.cvte.scm.wip.domain.core.scm.dto.query.SysOrgOrganizationVQuery;
import com.cvte.scm.wip.domain.core.scm.vo.SysOrgOrganizationVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScmViewCommonMapper {
    List<SysOrgOrganizationVO> listSysOrgOrganizationVO(SysOrgOrganizationVQuery query);

    String getFactoryCodeById(@Param("factoryId") String factoryId);
}