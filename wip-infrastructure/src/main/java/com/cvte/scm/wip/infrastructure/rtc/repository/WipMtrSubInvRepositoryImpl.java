package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrSubInvRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import com.cvte.scm.wip.infrastructure.rtc.mapper.WipMtrSubInvMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
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
    public List<WipMtrSubInvVO> selectInvLot(String organizationId, String factoryId, String itemId, String subinventoryCode, String moId) {
        return wipMtrSubInvMapper.selectLotControl(organizationId, factoryId, itemId, subinventoryCode, moId);
    }

    @Override
    public List<WipMtrSubInvVO> selectByItem(String organizationId, String factoryId, String itemId, Collection<String> lotNumbers) {
        // 获取批次
        List<WipMtrSubInvVO> queryList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(lotNumbers)) {
            for (String lotNumber : lotNumbers) {
                WipMtrSubInvVO mtrSubInvVO = new WipMtrSubInvVO();
                mtrSubInvVO.setOrganizationId(organizationId)
                        .setFactoryId(factoryId)
                        .setInventoryItemId(itemId)
                        .setLotNumber(lotNumber);
                queryList.add(mtrSubInvVO);
            }
        } else {
            WipMtrSubInvVO mtrSubInvVO = new WipMtrSubInvVO();
            mtrSubInvVO.setOrganizationId(organizationId)
                    .setFactoryId(factoryId)
                    .setInventoryItemId(itemId);
            queryList.add(mtrSubInvVO);
        }
        return this.selectByVO(queryList);
    }

}
