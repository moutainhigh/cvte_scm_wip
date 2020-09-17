package com.cvte.scm.wip.infrastructure.rtc.mapper;

import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;
import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcLDO;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-09-08
 */
public interface WipMtrRtcLMapper extends CommonMapper<WipMtrRtcLDO> {

    /**
     * 获取工单相同的不同领料单 指定工序 各个物料 的已申请未过账数量
     * @author xueyuting
     * @since 2020/9/17 8:17 下午
     * @param queryVO headerId非空时, 排除这个headerId对应的领料单
     */
    List<WipReqItemVO> batchSumMoUnPostQty(@Param("queryVO") WipMtrRtcLineQueryVO queryVO);

    /**
     * 获取不同领料单 相同工厂相同子库下 各个物料 的已申请未过账数量
     * @author xueyuting
     * @since 2020/9/17 8:17 下午
     * @param queryVO headerId非空时, 排除这个headerId对应的领料单
     */
    List<WipReqItemVO> batchSumUnPostQty(@Param("queryVO") WipMtrRtcLineQueryVO queryVO);

}