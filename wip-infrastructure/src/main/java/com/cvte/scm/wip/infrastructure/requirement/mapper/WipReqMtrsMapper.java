package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqMtrsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jf
 * @since 2020-03-04
 */
public interface WipReqMtrsMapper extends CommonMapper<WipReqMtrsDO> {

    /**
     * 查询物料的替代料(包括联络函替代)
     * @since 2020/8/3 3:27 下午
     * @author xueyuting
     */
    List<String> selectMtrsItemNo(@Param("headerId") String headerId, @Param("itemNo") String itemNo);

    /**
     * 查询物料的联络函替代料
     * @since 2020/8/3 3:27 下午
     * @author xueyuting
     */
    List<String> selectSubRuleMtrsItemNo(@Param("headerId") String headerId, @Param("itemId") String itemId);

}