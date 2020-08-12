package com.cvte.scm.wip.domain.core.item.repository;

import com.cvte.scm.wip.domain.core.item.entity.ScmItemEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:23
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ScmItemRepository {

    /* 根据指定组织和物料编码获取物料ID */
    String getItemId(String itemNo);

    /* 根据指定组织和物料ID获取物料编码 */
    String getItemNo(String itemId);

    /* 根据组织ID以及物料编码列表，获取数据库中存在的数据 */
    Set<String> getValidItemNos(String[] itemNos);

    List<ScmItemEntity> selectByItemNos(String organizationId, Iterable<String> itemNos);

    Map<String, String> selectNoById(Iterable<String> itemIds);

}
