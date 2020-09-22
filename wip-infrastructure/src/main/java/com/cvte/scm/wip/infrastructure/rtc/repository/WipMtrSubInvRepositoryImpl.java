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

    @Override
    public List<WipMtrSubInvVO> selectReworkControl(String organizationId, String factoryId, String itemId, String moId, String subinventoryCode) {
        return wipMtrSubInvMapper.selectReworkControl(organizationId, factoryId, itemId, moId, subinventoryCode);
    }

    @Override
    public List<WipMtrSubInvVO> selectConfigControl(String organizationId, String factoryId, String itemId, String subinventoryCode) {
        return wipMtrSubInvMapper.selectConfigControl(organizationId, factoryId, itemId, subinventoryCode);
    }

    @Override
    public List<WipMtrSubInvVO> selectWeakControl(String organizationId, String factoryId, String itemId, String subinventoryCode) {
        return wipMtrSubInvMapper.selectWeakControl(organizationId, factoryId, itemId, subinventoryCode);
    }

}
