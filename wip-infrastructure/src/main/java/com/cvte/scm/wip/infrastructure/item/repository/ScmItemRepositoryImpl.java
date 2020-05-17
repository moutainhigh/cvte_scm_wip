package com.cvte.scm.wip.infrastructure.item.repository;

import com.cvte.scm.wip.domain.core.item.entity.ScmItemEntity;
import com.cvte.scm.wip.domain.core.item.repository.ScmItemRepository;
import com.cvte.scm.wip.infrastructure.item.mapper.ScmItemMapper;
import com.cvte.scm.wip.infrastructure.item.mapper.dataobject.ScmItemDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:24
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ScmItemRepositoryImpl implements ScmItemRepository {

    private ScmItemMapper scmItemMapper;

    public ScmItemRepositoryImpl(ScmItemMapper scmItemMapper) {
        this.scmItemMapper = scmItemMapper;
    }

    @Override
    public String getItemId(String organizationId, String itemNo) {
        return scmItemMapper.getItemId(organizationId, itemNo);
    }

    @Override
    public String getItemNo(String organizationId, String itemId) {
        return scmItemMapper.getItemNo(organizationId, itemId);
    }

    @Override
    public Set<String> getValidItemNos(String organizationId, String[] itemNos) {
        return scmItemMapper.getValidItemNos(organizationId, itemNos);
    }

    @Override
    public List<ScmItemEntity> selectByItemNos(String organizationId, Iterable<String> itemNos) {
        Example example = new Example(ScmItemDO.class);
        example.createCriteria().andEqualTo("organizationId", organizationId).andIn("itemNo", itemNos);
        List<ScmItemDO> itemDOList = scmItemMapper.selectByExample(example);

        List<ScmItemEntity> itemEntityList = new ArrayList<>();
        for (ScmItemDO itemDO : itemDOList) {
            ScmItemEntity itemEntity = ScmItemDO.buildEntity(itemDO);
            itemEntityList.add(itemEntity);
        }
        return itemEntityList;
    }

}
