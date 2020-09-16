package com.cvte.scm.wip.infrastructure.item.repository;

import com.cvte.scm.wip.domain.common.item.entity.MdItemEntity;
import com.cvte.scm.wip.domain.common.item.repository.MdItemRepository;
import com.cvte.scm.wip.infrastructure.item.mapper.MdItemMapper;
import com.cvte.scm.wip.infrastructure.item.mapper.dataobject.MdItemDO;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqMtrsMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:24
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class MdItemRepositoryImpl implements MdItemRepository {

    private MdItemMapper mdItemMapper;

    private WipReqMtrsMapper wipReqMtrsMapper;

    public MdItemRepositoryImpl(MdItemMapper mdItemMapper, WipReqMtrsMapper wipReqMtrsMapper) {
        this.mdItemMapper = mdItemMapper;
        this.wipReqMtrsMapper = wipReqMtrsMapper;
    }

    @Override
    public MdItemEntity getItem(String itemid) {
        MdItemDO mdItemDO = mdItemMapper.selectOne(new MdItemDO().setInventoryItemId(itemid));
        return MdItemDO.buildEntity(mdItemDO);
    }

    @Override
    public int getItemListCount(List<MdItemEntity> list) {

        List<MdItemDO> mdItemDOS = mdItemMapper.selectList(list);
        return mdItemDOS.size();
    }
}
