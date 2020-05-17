package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipEbsReqLogDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-03-12
 */
public interface WipEbsReqLogMapper extends CommonMapper<WipEbsReqLogDO> {

    /**
     * 查询#{timeFrom}到#{timeTo}之间处理结果在#{processStatus}的数据
     * @since 2020/3/12 5:05 下午
     * @author xueyuting
     * @param timeFrom 开始时间
     * @param timeTo 结束时间
     * @param processStatus 处理结果
     */
    List<WipEbsReqLogDO> selectBetweenTimeInStatus(@Param("timeFrom") Date timeFrom, @Param("timeTo") Date timeTo, @Param("processStatus") String... processStatus);

}