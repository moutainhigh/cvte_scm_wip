package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.XxwipMoInterfaceEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/18 18:59
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface XxwipMoInterfaceRepository {

    void batchInsert(List<XxwipMoInterfaceEntity> moInterfaceList);

    /**
     * 调用存储过程，处理变更信息。
     */
    String[] callProcedure(String pWipId, String pGroupId);

}
