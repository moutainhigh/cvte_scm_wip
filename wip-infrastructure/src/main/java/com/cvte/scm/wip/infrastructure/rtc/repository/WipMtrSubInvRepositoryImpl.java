package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrSubInvRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import com.cvte.scm.wip.infrastructure.rtc.mapper.WipMtrSubInvMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 12:06
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipMtrSubInvRepositoryImpl implements WipMtrSubInvRepository {

    private WipMtrSubInvMapper wipMtrSubInvMapper;

    public WipMtrSubInvRepositoryImpl(WipMtrSubInvMapper wipMtrSubInvMapper) {
        this.wipMtrSubInvMapper = wipMtrSubInvMapper;
    }

    @Override
    public List<WipMtrSubInvVO> selectByVO(List<WipMtrSubInvVO> subInvVOList) {
        return wipMtrSubInvMapper.selectByVO(subInvVOList);
    }

}
