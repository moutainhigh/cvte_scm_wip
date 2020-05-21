package com.cvte.scm.wip.spi.changeorder;

import com.cvte.scm.wip.domain.core.changeorder.service.SourceChangeOrderService;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderBuildVO;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderDetailBuildVO;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderQueryVO;
import com.cvte.scm.wip.spi.changeorder.DTO.SourceChangeOrderDTO;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 11:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class SourceChangeOrderServiceImpl implements SourceChangeOrderService {

    @Override
    public List<ChangeOrderBuildVO> querySourceChangeOrder(ChangeOrderQueryVO queryVO) {
        List<SourceChangeOrderDTO> changeOrderDTOList = requestEbsChangeOrder(queryVO);
        return parseDtoToVo(changeOrderDTOList);
    }

    private List<SourceChangeOrderDTO> requestEbsChangeOrder(ChangeOrderQueryVO queryVO) {
        return null;
    }

    @VisibleForTesting
    protected List<ChangeOrderBuildVO> parseDtoToVo(List<SourceChangeOrderDTO> sourceOrderList) {
        List<ChangeOrderBuildVO> orderBuildVOList = new ArrayList<>();

        Map<String, List<SourceChangeOrderDTO>> sourceOrderMap = sourceOrderList.stream()
                .collect(Collectors.groupingBy(order -> order.getBillId() + order.getOrganizationId()));
        for (Map.Entry<String, List<SourceChangeOrderDTO>> entry : sourceOrderMap.entrySet()) {
            List<SourceChangeOrderDTO> sourceChangeOrderDTOList = entry.getValue();

            // 随机取一个, 生成更改单头构造对象
            SourceChangeOrderDTO randomOne = sourceChangeOrderDTOList.get(0);
            ChangeOrderBuildVO orderBuildVO = SourceChangeOrderDTO.buildHeaderVO(randomOne);

            // 生成所有更改单行构造对象
            List<ChangeOrderDetailBuildVO> orderDetailBuildVOList = new ArrayList<>();
            for (SourceChangeOrderDTO sourceChangeOrderDTO : sourceChangeOrderDTOList) {
                orderDetailBuildVOList.add(SourceChangeOrderDTO.buildDetailVO(sourceChangeOrderDTO));
            }

            orderBuildVO.setDetailVOList(orderDetailBuildVOList);
            orderBuildVOList.add(orderBuildVO);
        }
        return orderBuildVOList;
    }
}
