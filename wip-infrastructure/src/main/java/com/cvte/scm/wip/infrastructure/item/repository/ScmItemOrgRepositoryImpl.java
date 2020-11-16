package com.cvte.scm.wip.infrastructure.item.repository;

import com.cvte.scm.wip.domain.core.item.entity.ScmItemOrgEntity;
import com.cvte.scm.wip.domain.core.item.repository.ScmItemOrgRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.item.mapper.ScmItemOrgMapper;
import com.cvte.scm.wip.infrastructure.item.mapper.dataobject.ScmItemOrgDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/19 11:33
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ScmItemOrgRepositoryImpl
        extends WipBaseRepositoryImpl<ScmItemOrgMapper, ScmItemOrgDO, ScmItemOrgEntity>
        implements ScmItemOrgRepository {

    @Override
    public List<ScmItemOrgEntity> selectByIds(String organizationId, Collection<String> itemIds) {
        Example example = new Example(ScmItemOrgDO.class);
        example.createCriteria()
                .andEqualTo("organizationId", organizationId)
                .andIn("itemId", itemIds);
        return batchBuildEntity(mapper.selectByExample(example));
    }

}
