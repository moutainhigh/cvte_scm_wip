package com.cvte.scm.wip.infrastructure.sys.bu.repository;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.common.bu.entity.SysBuEntity;
import com.cvte.scm.wip.domain.common.bu.repository.SysBuRepository;
import com.cvte.scm.wip.infrastructure.sys.bu.mapper.SysBuMapper;
import com.cvte.scm.wip.infrastructure.sys.bu.mapper.dataobject.SysBuDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/26 15:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class SysBuRepositoryImpl implements SysBuRepository {

    private SysBuMapper sysBuMapper;

    public SysBuRepositoryImpl(SysBuMapper sysBuMapper) {
        this.sysBuMapper = sysBuMapper;
    }

    @Override
    public List<SysBuEntity> selectAll() {
        List<SysBuDO> buDOList = sysBuMapper.selectAll();
        if (ListUtil.empty(buDOList)) {
            return null;
        }
        return SysBuDO.batchBuildEntity(buDOList);
    }

}
