package com.cvte.scm.wip.infrastructure.item.repository;

import com.cvte.scm.wip.domain.core.item.entity.ScmItemEntity;
import com.cvte.scm.wip.domain.core.item.repository.ScmItemRepository;
import com.cvte.scm.wip.infrastructure.item.mapper.ScmItemMapper;
import com.cvte.scm.wip.infrastructure.item.mapper.dataobject.ScmItemDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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
    public String getItemId(String itemNo) {
        return scmItemMapper.getItemId(itemNo);
    }

    @Override
    public String getItemNo(String itemId) {
        return scmItemMapper.getItemNo(itemId);
    }

    @Override
    public Set<String> getValidItemNos(String[] itemNos) {
        return scmItemMapper.getValidItemNos(itemNos);
    }

    @Override
    public List<ScmItemEntity> selectByItemNos(String organizationId, Iterable<String> itemNos) {
        Example example = new Example(ScmItemDO.class);
        example.createCriteria().andIn("itemNo", itemNos);
        List<ScmItemDO> itemDOList = scmItemMapper.selectByExample(example);

        List<ScmItemEntity> itemEntityList = new ArrayList<>();
        for (ScmItemDO itemDO : itemDOList) {
            ScmItemEntity itemEntity = ScmItemDO.buildEntity(itemDO, organizationId);
            itemEntityList.add(itemEntity);
        }
        return itemEntityList;
    }

    @Override
    public Map<String, String> selectNoById(Iterable<String> itemIds) {
        Example example = new Example(ScmItemDO.class);
        example.createCriteria().andIn("itemId", itemIds);
        List<ScmItemDO> itemDOList = scmItemMapper.selectByExample(example);

        Map<String, String> resultMap = new HashMap<>();
        for (ScmItemDO item : itemDOList) {
            resultMap.put(item.getItemId(), item.getItemNo());
        }
        return resultMap;
    }

}
