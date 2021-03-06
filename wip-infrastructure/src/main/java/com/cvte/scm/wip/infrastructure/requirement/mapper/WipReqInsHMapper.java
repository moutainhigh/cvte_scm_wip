package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqInsHeaderDO;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-05-22
 */
public interface WipReqInsHMapper extends CommonMapper<WipReqInsHeaderDO> {

    /**
     * 查询已备料的指令, 返回指令对应的更改单号
     * @since 2020/6/19 8:51 下午
     * @author xueyuting
     * @param idList 指令ID列表
     */
    List<String> selectPreparedById(@Param("idList") List<String> idList);

    /**
     * 获取需要自动执行的更改单
     * @since 2020/9/27 9:29 上午
     * @author xueyuting
     * @param organizationIds 组织ID, 为空时所有组织
     * @param factoryIds 工厂ID, 为空时所有工厂
     */
    List<String> getAutoConfirm(@Param("organizationIds") List<String> organizationIds, @Param("factoryIds") List<String> factoryIds, @Param("billTypes") List<String> billTypes);

}