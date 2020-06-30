package com.cvte.scm.wip.domain.common.bu.service;

import com.cvte.scm.wip.domain.common.bu.entity.SysBuDeptEntity;
import com.cvte.scm.wip.domain.common.bu.repository.SysBuDeptRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/28 13:14
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class SysBuDeptService {

    private SysBuDeptRepository sysBuDeptRepository;

    public SysBuDeptService(SysBuDeptRepository sysBuDeptRepository) {
        this.sysBuDeptRepository = sysBuDeptRepository;
    }

    public List<SysBuDeptEntity> getByBuCode(String buCode) {
        return sysBuDeptRepository.selectByBuCode(buCode);
    }

    public SysBuDeptEntity getBuDeptByTypeCode(String typeCode) {
        return sysBuDeptRepository.selectBuDeptByTypeCode(typeCode);
    }

}
