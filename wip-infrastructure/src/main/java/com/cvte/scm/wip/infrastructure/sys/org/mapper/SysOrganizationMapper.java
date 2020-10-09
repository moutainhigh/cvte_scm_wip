package com.cvte.scm.wip.infrastructure.sys.org.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.sys.org.mapper.dataobject.SysOrgDO;
import org.apache.ibatis.annotations.Param;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/10/8 09:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface SysOrganizationMapper extends CommonMapper<SysOrgDO> {

    String getOrgCodeById(@Param("organizationId") String organizationId);

    String getFactoryCodeById(@Param("factoryId") String factoryId);

}
