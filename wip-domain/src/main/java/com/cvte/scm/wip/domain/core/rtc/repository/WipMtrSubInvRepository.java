package com.cvte.scm.wip.domain.core.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;

import java.util.Collection;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 11:59
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipMtrSubInvRepository {

    List<WipMtrSubInvVO> selectByVO(List<WipMtrSubInvVO> subInvVOList);

    /**
     * 获取库存批次现有量及工单物料批次
     * @since 2020/9/21 4:35 下午
     * @author xueyuting
     */
    List<WipMtrSubInvVO> selectInvLot(String organizationId, String factoryId, String itemId, String subinventoryCode, String moId);

    /**
     *
     * @since 2020/11/6 4:39 下午
     * @author xueyuting
     * @param lotNumbers 投料批次, 可为空
     */
    List<WipMtrSubInvVO> selectByItem(String organizationId, String factoryId, String itemId, Collection<String> lotNumbers);

}
