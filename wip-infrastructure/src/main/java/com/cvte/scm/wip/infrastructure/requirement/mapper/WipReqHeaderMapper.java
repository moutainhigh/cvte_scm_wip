package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqHeaderDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jf
 * @since 2019-12-30
 */
public interface WipReqHeaderMapper extends CommonMapper<WipReqHeaderDO> {

    /* 根据投料单头ID获取指定的源ID，用于EBS回写。 */
    String getSourceId(@Param("headerId") String headerId);

    /* 获取工单信息，主要用投料单头的增量写入。 */
    List<WipReqHeaderDO> selectAddedData(@Param("organizationIdList") List<Integer> organizationIdList);
}