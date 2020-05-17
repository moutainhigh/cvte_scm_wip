package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 15:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReqLineRepository {

    WipReqLineEntity selectById(String id);

    List<WipReqLineEntity> selectList(WipReqLineEntity queryEntity);

    List<WipReqLineEntity> selectByExample(Example example);

    void insertSelective(WipReqLineEntity lineEntity);

    void updateSelectiveById(WipReqLineEntity lineEntity);

    void writeIncrementalData(List<String> wipEntityIdList, List<Integer> organizationIdList);

}
