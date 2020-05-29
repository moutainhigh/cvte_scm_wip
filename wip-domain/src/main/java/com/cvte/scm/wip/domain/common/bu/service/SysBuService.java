package com.cvte.scm.wip.domain.common.bu.service;

import com.cvte.scm.wip.domain.common.bu.entity.SysBuEntity;
import com.cvte.scm.wip.domain.common.bu.repository.SysBuRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/28 13:07
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class SysBuService {

    private SysBuRepository sysBuRepository;

    public SysBuService(SysBuRepository sysBuRepository) {
        this.sysBuRepository = sysBuRepository;
    }

    public List<SysBuEntity> getAll() {
        return sysBuRepository.selectAll();
    }

}
