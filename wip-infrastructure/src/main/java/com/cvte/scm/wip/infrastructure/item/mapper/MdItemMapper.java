package com.cvte.scm.wip.infrastructure.item.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.common.item.entity.MdItemEntity;
import com.cvte.scm.wip.infrastructure.item.mapper.dataobject.MdItemDO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 2:02 下午
 */
public interface MdItemMapper extends CommonMapper<MdItemDO> {

    @Select({"<script>" +
            "select " +
            "item.inventory_item_id,item.item_code,item.part_family,item.item_desc,item.inventory_item_status_code  " +
            "from scm.md_item item " +
            "where  item.inventory_item_id in " +
            "<foreach collection='list' item='v' open='(' separator=',' close=')'>" +
                "#{v.inventoryItemId}" +
            "</foreach>" +
            "</script>"})
    List<MdItemDO> selectList(List<MdItemEntity> list);

    @Select(
            "select " +
            "item.inventory_item_id,item.item_code,item.partFamily,item.itemDesc,item.inventoryItemStatusCode,org.BUYER_CHN_NAME " +
            "from scm.md_item item " +
            "left join scm.md_item_org org on org.inventory_item_id = item.inventory_item_id " +
            "where  item.itemId in #{itemId}")
    MdItemDO selectItem(String itemId);
}
