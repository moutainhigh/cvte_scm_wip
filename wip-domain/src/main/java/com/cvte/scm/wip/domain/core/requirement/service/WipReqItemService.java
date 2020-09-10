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
     * 获取工单工序投料信息
     * @since 2020/9/9 10:24 上午
     * @author xueyuting
     */
    public List<WipReqItemVO> getReqItemList(WipMtrRtcHeaderBuildVO wipMtrRtcHeaderBuildVO) {
        WipReqLineKeyQueryVO reqLineQueryVO = WipReqLineKeyQueryVO.build(wipMtrRtcHeaderBuildVO);
        // 查询工单投料信息
        List<WipReqItemVO> reqItemVOList = wipReqLineRepository.selectReqItem(reqLineQueryVO);
        // 查询投料申请未过账数量
        WipMtrRtcLineQueryVO wipMtrRtcLineQueryVO = WipMtrRtcLineQueryVO.buildForUnPost(wipMtrRtcHeaderBuildVO.getOrganizationId(), wipMtrRtcHeaderBuildVO.getHeaderId(), wipMtrRtcHeaderBuildVO.getMoId(),
                wipMtrRtcHeaderBuildVO.getBillType(), wipMtrRtcHeaderBuildVO.getWkpNo(), wipMtrRtcHeaderBuildVO.getItemList());
        List<WipReqItemVO> unPostReqItemVOList = wipMtrRtcLineRepository.batchSumUnPostQtyExceptCurrent(wipMtrRtcLineQueryVO);
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
