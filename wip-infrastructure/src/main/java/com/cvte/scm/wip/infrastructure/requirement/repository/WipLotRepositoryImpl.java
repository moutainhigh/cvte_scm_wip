package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.repository.WipLotRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipLotMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/2 16:05
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipLotRepositoryImpl implements WipLotRepository {

    private WipLotMapper wipLotMapper;

    public WipLotRepositoryImpl(WipLotMapper wipLotMapper) {
        this.wipLotMapper = wipLotMapper;
    }

    @Override
    public List<WipLotVO> selectByHeaderId(String headerId) {
        return wipLotMapper.selectByHeaderId(headerId);
    }
}
