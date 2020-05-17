package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqInterfaceDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author : jf
 * Date    : 2020.01.06
 * Time    : 14:46
 * Email   ：jiangfeng7128@cvte.com
 */
public interface WipReqInterfaceMapper extends CommonMapper<WipReqInterfaceDO> {

    /* 根据 group_id 查询未处理的投料单变更数据。 */
    List<WipReqInterfaceDO> selectByGroupIds(@Param("groupIds") String[] groupIds, @Param("proceed") String proceed);

    /* 根据 interface_id 查询未处理的投料单变更数据。 */
    List<WipReqInterfaceDO> selectByInterfaceInIds(@Param("interfaceInIds") String[] interfaceIds, @Param("proceed") String proceed);

    /* 根据 interface_id 查询待确认变更的接口数据。 */
    List<WipReqInterfaceDO> selectByNeedConfirm(@Param("interfaceInIds") String[] interfaceIds, @Param("needConfirm") String needConfirm);

    /* 根据投料单头表的 ID 查询投料单变更接口表中，待确认变更的数量。 */
    int getNotSolvedNumber(@Param("headerId") String headerId, @Param("needConfirm") String needConfirm);

    /* 获取所有未处理的接口数据 */
    List<WipReqInterfaceDO> selectOmissionData(@Param("proceed") String proceed);

    /**
     * 查询#{timeFrom}到#{timeTo}之间处理结果在#{processStatus}的数据
     *
     * @param timeFrom 开始时间
     * @param timeTo   结束时间
     * @param proceed  处理结果
     * @author xueyuting
     * @since 2020/3/12 5:05 下午
     */
    List<WipReqInterfaceDO> selectBetweenTimeInStatus(@Param("timeFrom") Date timeFrom, @Param("timeTo") Date timeTo, @Param("proceed") String... proceed);
}