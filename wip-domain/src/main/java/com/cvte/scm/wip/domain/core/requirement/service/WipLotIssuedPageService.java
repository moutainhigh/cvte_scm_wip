package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrSubInvRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/11/6 16:24
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipLotIssuedPageService {

    private WipMtrSubInvRepository wipMtrSubInvRepository;
    private WipLotOnHandService wipLotOnHandService;

    public WipLotIssuedPageService(WipMtrSubInvRepository wipMtrSubInvRepository, WipLotOnHandService wipLotOnHandService) {
        this.wipMtrSubInvRepository = wipMtrSubInvRepository;
        this.wipLotOnHandService = wipLotOnHandService;
    }

    public List<WipMtrSubInvVO> getLot(WipMtrSubInvVO subInvVO) {
        String organizationId = subInvVO.getOrganizationId();
        String itemId = subInvVO.getInventoryItemId();
        if (StringUtils.isBlank(organizationId) || StringUtils.isBlank(itemId)) {
            throw new ParamsIncorrectException("参数不可为空");
        }

        // 库存
        List<String> lotList = new ArrayList<>();
        if (StringUtils.isNotBlank(subInvVO.getLotNumber())) {
            lotList.add(subInvVO.getLotNumber());
        }
        List<WipMtrSubInvVO> mtrSubInvVOList = wipMtrSubInvRepository.selectByItem(subInvVO.getOrganizationId(), null, subInvVO.getInventoryItemId(), lotList);
        List<WipMtrSubInvVO> totalInvVOList = new ArrayList<>(mtrSubInvVOList);

        // 在途
        totalInvVOList.addAll(wipLotOnHandService.getOnHand(subInvVO.getOrganizationId(), subInvVO.getInventoryItemId(), null));

        if (StringUtils.isNotBlank(subInvVO.getLotNumber())) {
            // 模糊匹配查询批次
            totalInvVOList.removeIf(vo -> StringUtils.isBlank(vo.getLotNumber()) || vo.getLotNumber().contains(subInvVO.getLotNumber()));
        }
        return totalInvVOList;
    }

}
