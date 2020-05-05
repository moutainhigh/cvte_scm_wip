package com.cvte.scm.demo.demo.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.demo.demo.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:40
 */
@Api(tags = "案例")
@RestController
@RequestMapping("/admin/demo")
@Slf4j
public class DemoAdminController {

    @Autowired
    private DemoService demoService;

    @ApiOperation(value = "测试接口", notes = "测试接口")
    @GetMapping("/value")
    public RestResponse getDemoValue() {
        return demoService.getDemoValue();
    }
}
