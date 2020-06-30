package com.cvte.scm.wip.domain.core.rework.repository;

import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillHeaderEntity;
import com.cvte.scm.wip.domain.core.rework.valueobject.ApiReworkBillVO;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 10:42
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReworkBillHeaderRepository {

    WipReworkBillHeaderEntity selectByBillNo(String billNo);

    List<WipReworkBillHeaderEntity> selectByBillNo(List<String> billNoList);

    WipReworkBillHeaderEntity selectByBillId(String billId);

    List<WipReworkBillHeaderEntity> selectBySourceOrderAndBillNo(String sourceOrderId, List<String> billNoList);

    List<ApiReworkBillVO> selectByKeyList(List<String> keyList);

    Map<String, String> selectOrgAbbrNameMap(List<String> organizationIdList);

    Map<String, String> selectFactoryNameMap(List<String> factoryIdList);

    void batchInsertSelective(List<WipReworkBillHeaderEntity> billHeaderList);

    void update(WipReworkBillHeaderEntity billHeader);

    void batchUpdate(List<WipReworkBillHeaderEntity> billHeaderList);

}
