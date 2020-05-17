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

    /* 根据指定组织和物料编码获取物料ID */
    String getItemId(@Param("organizationId") String organizationId, @Param("itemNo") String itemNo);

    /* 根据指定组织和物料ID获取物料编码 */
    String getItemNo(@Param("organizationId") String organizationId, @Param("itemId") String itemId);

    /* 根据组织ID以及物料编码列表，获取数据库中存在的数据 */
    Set<String> getValidItemNos(@Param("organizationId") String organizationId, @Param("itemNos") String[] itemNos);

}
