package com.cvte.scm.wip.domain.core.rework.service;

import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkAvailableQtyVO;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkMoVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/24 16:18
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface OcsRwkBillService {

    List<WipRwkAvailableQtyVO> getAvailableQty(WipRwkMoVO rwkMoDTO, List<WipReworkMoEntity> rwkMoList);

}
