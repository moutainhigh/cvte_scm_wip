package com.cvte.scm.wip.domain.core.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;

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
     * 强管控批次
     * @since 2020/9/21 4:35 下午
     * @author xueyuting
     */
    List<WipMtrSubInvVO> selectLotControl(String organizationId, String factoryId, String itemId, String subinventoryCode);

    /**
     * 启用批次管控但无领料批次, 直接获取可用量
     * @since 2020/9/21 4:37 下午
     * @author xueyuting
     */
    List<WipMtrSubInvVO> selectWeakControl(String organizationId, String factoryId, String itemId, String subinventoryCode);

}
