package com.cvte.scm.wip.domain.core.rtc.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;

import java.util.List;

/**
 * 服务类
 *
 * @author author
 * @since 2020-09-08
 */
public interface WipMtrRtcLineRepository extends WipBaseRepository<WipMtrRtcLineEntity> {

    List<WipMtrRtcLineEntity> selectByHeaderId(String headerId);

    /**
     * 获取工单相同的不同领料单 指定工序 各个物料 的已申请未过账数量
     * 维度是 投料+工单, 可用于计算 工单可领数量
     * @author xueyuting
     * @since 2020/9/17 8:17 下午
     * @param queryVO headerId非空时, 排除这个headerId对应的领料单
     */
    List<WipReqItemVO> batchSumMoUnPostQty(WipMtrRtcLineQueryVO queryVO);

    /**
     * 获取不同领料单 相同工厂相同子库下 各个物料 的已申请未过账数量
     * 维度是 物料, 可用于计算 物料可用量
     * @author xueyuting
     * @since 2020/9/17 8:17 下午
     * @param queryVO headerId非空时, 排除这个headerId对应的领料单
     */
    List<WipReqItemVO> batchSumUnPostQty(WipMtrRtcLineQueryVO queryVO);

}
