package com.cvte.scm.wip.domain.core.rework.repository;

import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkBillLVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/16 17:14
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReworkBillLineRepository {

    List<WipRwkBillLVO> sumRwkQty(List<String> factoryIdList, List<String> moLotNoList);

    List<WipReworkBillLineEntity> selectByBillId(String billId);

    List<WipReworkBillLineEntity> selectByBillId(List<String> billIdList);

    void batchInsert(List<WipReworkBillLineEntity> billLineList);

    void batchUpdate(List<WipReworkBillLineEntity> billLineList);

}
