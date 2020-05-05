package com.cvte.scm.wip.domain.common.health.service;

import com.cvte.scm.wip.domain.common.health.repository.HealthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 16:21
 */
@Service
@Slf4j
public class HealthService {

    @Autowired
    private HealthRepository healthRepository;

    public String getMessage() {
        return healthRepository.getMessage();
    }
}
