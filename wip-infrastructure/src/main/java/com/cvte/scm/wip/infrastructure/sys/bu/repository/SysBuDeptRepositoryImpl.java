package com.cvte.scm.wip.infrastructure.sys.bu.repository;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.common.bu.entity.SysBuDeptEntity;
import com.cvte.scm.wip.domain.common.bu.repository.SysBuDeptRepository;
import com.cvte.scm.wip.infrastructure.sys.bu.mapper.SysBuDeptMapper;
import com.cvte.scm.wip.infrastructure.sys.bu.mapper.dataobject.SysBuDeptDO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/26 15:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class SysBuDeptRepositoryImpl implements SysBuDeptRepository {

    private SysBuDeptMapper sysBuDeptMapper;

    public SysBuDeptRepositoryImpl(SysBuDeptMapper sysBuDeptMapper) {
        this.sysBuDeptMapper = sysBuDeptMapper;
    }

    @Override
    public List<SysBuDeptEntity> selectByBuCode(String buCode) {
        List<SysBuDeptDO> deptDOList = sysBuDeptMapper.select(new SysBuDeptDO().setBuCode(buCode));
        if (ListUtil.empty(deptDOList)) {
            return null;
        }
        return SysBuDeptDO.batchBuildEntity(deptDOList);
    }

    @Override
    public SysBuDeptEntity selectBuDeptByTypeCode(String typeCode) {
        SysBuDeptDO deptDO = sysBuDeptMapper.selectBuDeptByTypeCode(typeCode);
        if (Objects.isNull(deptDO)) {
            return null;
        }
        return SysBuDeptDO.buildEntity(deptDO);
    }
}
