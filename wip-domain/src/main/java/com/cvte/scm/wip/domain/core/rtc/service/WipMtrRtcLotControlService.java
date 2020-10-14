package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.scm.wip.domain.core.rtc.valueobject.ScmLotControlVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 16:45
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipMtrRtcLotControlService {

    /**
     * 获取启用批次管控的物料ID
     * @since 2020/9/21 3:02 下午
     * @author xueyuting
     * @param organizationId 组织ID
     * @param itemIdList 物料ID列表, 为空时返回空列表
     */
    List<String> getLotControlItem(String organizationId, List<String> itemIdList);

    List<WipMtrSubInvVO> getItemLot(String organizationId, String factoryId, String itemId, String moId, String subinventoryCode);

    List<ScmLotControlVO> getWipLotControlConfig();

}
