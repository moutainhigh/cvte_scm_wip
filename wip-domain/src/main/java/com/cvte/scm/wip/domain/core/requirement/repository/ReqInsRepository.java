package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ReqInsRepository {

    void insert(ReqInsEntity entity);

    void update(ReqInsEntity entity);

    ReqInsEntity selectByKey(String insKey);

    List<ReqInsEntity> selectByAimHeaderId(String aimHeaderId, List<String> statusList);

    List<String> getPreparedById(@Param("idList") List<String> idList);

    /**
     * 获取需要自动执行的更改单
     * @since 2020/9/27 9:29 上午
     * @author xueyuting
     * @param organizationIds 组织ID, 为空时所有组织
     * @param factoryIds 工厂ID, 为空时所有工厂
     * @param billTypes
     */
    List<String> getAutoConfirm(List<String> organizationIds, List<String> factoryIds, List<String> billTypes);

}
