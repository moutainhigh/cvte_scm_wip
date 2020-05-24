package com.cvte.scm.wip.infrastructure.rework.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.csb.sys.base.entity.SysOrg;
import com.cvte.scm.wip.domain.core.rework.valueobject.ApiReworkBillVO;
import com.cvte.scm.wip.infrastructure.rework.mapper.dataobject.WipRwkBillHDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-03-23
 */
public interface WipRwkBillHMapper extends CommonMapper<WipRwkBillHDO> {

    SysOrg selectOrgByEbsCode(@Param("relationCodeStarter") String relationCodeStarter, @Param("ebsCode") String ebsCode);

    List<ApiReworkBillVO> selectByKeyList(@Param("billKeyList") List<String> billKeyList);

}