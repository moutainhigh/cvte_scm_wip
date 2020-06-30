package com.cvte.scm.wip.infrastructure.scm.mapper;

import com.cvte.scm.wip.domain.core.scm.dto.query.MdItemQuery;
import com.cvte.scm.wip.domain.core.scm.dto.query.SysOrgOrganizationVQuery;
import com.cvte.scm.wip.domain.core.scm.vo.MdItemVO;
import com.cvte.scm.wip.domain.core.scm.vo.SysBuDeptVO;
import com.cvte.scm.wip.domain.core.scm.vo.SysOrgOrganizationVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScmViewCommonMapper {
    List<SysOrgOrganizationVO> listSysOrgOrganizationVO(SysOrgOrganizationVQuery query);

    String getFactoryCodeById(@Param("factoryId") String factoryId);

    SysBuDeptVO getSysBuDeptVO(@Param("buCode") String buCode, @Param("deptCode") String deptCode);

    List<MdItemVO> listMdItemVO(MdItemQuery query);
}