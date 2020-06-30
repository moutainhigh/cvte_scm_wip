package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/2 16:04
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipLotRepository {

    List<WipLotVO> selectByHeaderId(String headerId);

}
