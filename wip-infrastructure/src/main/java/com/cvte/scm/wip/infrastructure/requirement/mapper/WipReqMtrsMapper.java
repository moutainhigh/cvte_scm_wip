package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqMtrsDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 查询该工单本身的替代料
     * @param headerId
     * @param itemId
     * @return
     */
    @Select("select * from wip.wip_req_mtrs wrm where exists(" +
            "select x.*  from wip.wip_req_mtrs x where x.header_id = #{headerId} "+
            "and x.item_id = #{itemId}" +
            " and x.header_id = wrm.header_id" +
            " and x.item_group = wrm.item_group " +
            ")")
    List<WipReqMtrsDO> getAllMtrs(@Param("headerId") String headerId,@Param("itemId") String itemId);

    /**
     * 查询是否存在此替代料
     * @param headerId
     * @param oldItemId
     * @param nowItemId
     * @return
     */
    @Select("select count(*) from wip.wip_req_mtrs wrm where exists(" +
            "select x.*  from wip.wip_req_mtrs x where x.header_id = #{headerId} " +
            "and x.item_id = #{oldItemId}" +
            " and x.header_id = wrm.header_id" +
            " and x.item_group = wrm.item_group " +
            ") and wrm.item_id =#{nowItemId}")
    int countReqMtrs(@Param("headerId")String headerId,@Param("oldItemId") String oldItemId,@Param("nowItemId") String nowItemId);
}