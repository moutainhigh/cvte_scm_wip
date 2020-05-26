package com.cvte.scm.wip.domain.common.bu.repository;

import com.cvte.scm.wip.domain.common.bu.entity.SysBuEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/26 15:31
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface SysBuRepository {

    List<SysBuEntity> selectAll();

}
