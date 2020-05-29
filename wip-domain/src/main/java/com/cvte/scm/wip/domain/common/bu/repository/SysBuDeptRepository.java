package com.cvte.scm.wip.domain.common.bu.repository;

import com.cvte.scm.wip.domain.common.bu.entity.SysBuDeptEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/26 15:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface SysBuDeptRepository {

    List<SysBuDeptEntity> selectByBuCode(String buCode);

    SysBuDeptEntity selectBuDeptByTypeCode(String typeCode);

}
