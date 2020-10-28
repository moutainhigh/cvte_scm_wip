package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLineDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : jf
 * Date    : 2019.12.30
 * Time    : 10:56
 * Email   ：jiangfeng7128@cvte.com
 */
public interface WipReqLinesMapper extends CommonMapper<WipReqLineDO> {

    /* 增量写入投料单行数据 */
    void writeIncrementalData(@Param("wipEntityIdList") List<String> wipEntityIdList, @Param("organizationIdList") List<Integer> organizationIdList);

    void writeLackLines(@Param("wipEntityIdList") List<String> wipEntityIdList, @Param("organizationIdList") List<Integer> organizationIdList);

    List<WipReqLineEntity> selectByItemDim(@Param("organizationId") String organizationId,
                                           @Param("headerId") String headerId,
                                           @Param("wkpNo") String wkpNo,
                                           @Param("itemKey") String itemKey);

}