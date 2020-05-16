package com.cvte.scm.wip.infrastructure.client.sys.view;

import com.cvte.csb.sys.database.dto.request.DatabaseQueryDTO;
import com.cvte.csb.sys.view.dto.bean.PageResult;
import com.cvte.csb.sys.view.dto.request.SysViewPageParamDTO;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/1/2 18:06
  * @version : 1.0
  * email   : xueyuting@cvte.com tingyx96@qq.com
  */
@FeignClient("scm-bsm")
public interface SysViewApiClient {
    /**
     * 获取视图分页数据
     * @param sysViewPageParam 视图分页查询参数
     */
    @PostMapping({"/admin/v1/view/render"})
    FeignResult<PageResult> getViewPageDataByViewPageParam(@RequestBody SysViewPageParamDTO sysViewPageParam);
    /**
     * 执行查询返回结果集
     * @param databaseQueryDTO 视图查询SQL
     */
    @PostMapping({"/api/v1/database/execute_query"})
    List<Map<String, Object>> executeQuery(@RequestBody DatabaseQueryDTO databaseQueryDTO);
}
