package com.cvte.scm.wip.domain.common.item.repository;

import com.cvte.scm.wip.domain.common.item.entity.MdItemEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:23
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface MdItemRepository {


    MdItemEntity getItem(String itemid);

    int getItemListCount(List<MdItemEntity> list);
}
