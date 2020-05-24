package com.cvte.scm.wip.domain.core.rework.repository;

import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkMoVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/24 15:42
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReworkMoRepository {

    List<WipReworkMoEntity> selectByEntity(WipReworkMoEntity entity);

    List<WipReworkMoEntity> selectByParam(WipRwkMoVO moParam);

}
