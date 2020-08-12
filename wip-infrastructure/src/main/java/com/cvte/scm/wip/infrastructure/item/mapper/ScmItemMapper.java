package com.cvte.scm.wip.infrastructure.item.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.item.mapper.dataobject.ScmItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:18
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ScmItemMapper extends CommonMapper<ScmItemDO> {

    /* 根据物料编码获取物料ID */
    String getItemId(@Param("itemNo") String itemNo);

    /* 根据物料ID获取物料编码 */
    String getItemNo(@Param("itemId") String itemId);

    /* 根据物料编码列表，获取数据库中存在的数据 */
    Set<String> getValidItemNos(@Param("itemNos") String[] itemNos);

}
