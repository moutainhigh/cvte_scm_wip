package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:04
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReqHeaderRepository {

    String getSourceId(String headerId);

    List<WipReqHeaderEntity> selectList(WipReqHeaderEntity headerEntity);

    List<WipReqHeaderEntity> selectByExample(Example example);

    /* 获取工单信息，主要用投料单头的增量写入。 */
    List<WipReqHeaderEntity> selectAddedData(@Param("organizationIdList") List<Integer> organizationIdList);

    void updateStatusById(String billStatus, String headerId);

}