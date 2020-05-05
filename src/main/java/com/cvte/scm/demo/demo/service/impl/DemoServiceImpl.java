package com.cvte.scm.demo.demo.service.impl;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.demo.demo.mapper.DemoMapper;
import com.cvte.scm.demo.demo.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:34
 */
@Slf4j
@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoMapper demoMapper;

    @Override
    public RestResponse getDemoValue() {
        return ResponseFactory.getOkResponse(demoMapper.getDemoValue());
    }
}
