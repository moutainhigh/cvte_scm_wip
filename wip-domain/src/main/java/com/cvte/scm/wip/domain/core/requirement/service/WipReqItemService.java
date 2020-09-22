package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/10 15:25
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipReqItemService {

    private WipReqLineRepository wipReqLineRepository;
    private WipMtrRtcLineRepository wipMtrRtcLineRepository;

    public WipReqItemService(WipReqLineRepository wipReqLineRepository, WipMtrRtcLineRepository wipMtrRtcLineRepository) {
        this.wipReqLineRepository = wipReqLineRepository;
        this.wipMtrRtcLineRepository = wipMtrRtcLineRepository;
    }

    /**
     * 查询工单投料信息
     * @since 2020/9/22 11:21 上午
     * @author xueyuting
     * @param
     */
    public List<WipReqItemVO> getReqItem(WipReqLineKeyQueryVO reqLineQueryVO) {
        return wipReqLineRepository.selectReqItem(reqLineQueryVO);
    }

    /**
     * 获取工单投料及其未完成领料信息
     * @since 2020/9/9 10:24 上午
     * @author xueyuting
     */
    public List<WipReqItemVO> getReqItemWithUnPost(WipMtrRtcLineQueryVO wipMtrRtcLineQueryVO) {
        WipReqLineKeyQueryVO reqLineQueryVO = WipReqLineKeyQueryVO.buildForReqItem(wipMtrRtcLineQueryVO.getOrganizationId(), wipMtrRtcLineQueryVO.getMoId(), wipMtrRtcLineQueryVO.getWkpNo(), wipMtrRtcLineQueryVO.getItemKeyColl());
        // 查询工单投料信息
        List<WipReqItemVO> reqItemVOList = getReqItem(reqLineQueryVO);
        // 查询投料申请未过账数量
        List<WipReqItemVO> unPostReqItemVOList = wipMtrRtcLineRepository.batchSumMoUnPostQty(wipMtrRtcLineQueryVO);
        Map<String, WipReqItemVO> unPostReqItemVOMap = unPostReqItemVOList.stream().collect(Collectors.toMap(WipReqItemVO::getKey, Function.identity()));

        for (WipReqItemVO reqItemVO : reqItemVOList) {
            // 补充未过账数量
            WipReqItemVO wipReqItemVO = unPostReqItemVOMap.get(reqItemVO.getKey());
            if (Objects.nonNull(wipReqItemVO) && Objects.nonNull(wipReqItemVO.getUnPostQty())) {
                reqItemVO.setUnPostQty(wipReqItemVO.getUnPostQty());
            } else {
                reqItemVO.setUnPostQty(BigDecimal.ZERO);
            }
        }

        return reqItemVOList;
    }

}
