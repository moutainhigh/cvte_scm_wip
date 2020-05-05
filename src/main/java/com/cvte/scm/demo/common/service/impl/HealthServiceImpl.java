package com.cvte.scm.demo.common.service.impl;

import com.cvte.scm.demo.common.mapper.HealthMapper;
import com.cvte.scm.demo.common.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 16:21
 */
@Service
@Slf4j
public class HealthServiceImpl implements HealthService {

    @Autowired
    private HealthMapper healthMapper;

    @Override
    public String getMessage() {
        return healthMapper.getMessage();
    }
}
