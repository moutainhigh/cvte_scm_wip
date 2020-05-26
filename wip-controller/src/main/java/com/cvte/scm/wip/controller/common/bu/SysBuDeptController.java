package com.cvte.scm.wip.controller.common.bu;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.common.bu.entity.SysBuDeptEntity;
import com.cvte.scm.wip.domain.common.bu.service.SysBuDeptService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/28 13:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@RequestMapping("/admin/sys/buDept")
public class SysBuDeptController {

    private SysBuDeptService sysBuDeptService;

    public SysBuDeptController(SysBuDeptService sysBuDeptService) {
        this.sysBuDeptService = sysBuDeptService;
    }

    @ApiOperation(value = "根据事业部编码获取部门信息")
    @GetMapping("/listByBu/{buCode}")
    public RestResponse list(@PathVariable("buCode") String buCode) {
        List<SysBuDeptEntity> sysBuDeptList = sysBuDeptService.getByBuCode(buCode);
        return new RestResponse().setData(sysBuDeptList);
    }

    @ApiOperation(value = "根据订单类型获取事业部信息")
    @GetMapping("/getByTypeCode/{typeCode}")
    public RestResponse getByBtName(@PathVariable("typeCode") String typeCode) {
        SysBuDeptEntity sysBuDept = sysBuDeptService.getBuDeptByTypeCode(typeCode);
        return new RestResponse().setData(sysBuDept);
    }
}
