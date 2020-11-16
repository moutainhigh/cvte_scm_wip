package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.rtc.valueobject.ScmLotControlVO;
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

    /* 获取已发放的工单信息，主要用投料单头的增量写入。 */
    List<WipReqHeaderDO> selectDelivered(@Param("organizationIdList") List<Integer> organizationIdList, @Param("factoryId") String factoryId);

    /* 获取工单信息，主要用投料单头的增量写入。 */
    List<WipReqHeaderDO> selectSpecific(@Param("organizationIdList") List<Integer> organizationIdList, @Param("factoryId") String factoryId, @Param("lotControlVOList") List<ScmLotControlVO> lotControlVOList);

    List<String> filterCachedUndelivered(@Param("wipEntityIdList") List<String> wipEntityIdList);

    /* 判断货位信息在投料单头下是否存在 */
    boolean existLotNumber(@Param("headerId") Integer headerId, @Param("lotNumber") String lotNumber);
}