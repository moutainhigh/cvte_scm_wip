package com.cvte.scm.wip.infrastructure.client.datapermission;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 15:35
 */
@FeignClient("scm-bsm")
@RequestMapping("/api/v1/datapermission")
public interface DataPermissionApiClient {

    /**
     * 根据用户和资源编码获取数据权限SQL
     * @param userId 用户编码
     * @param resourceCode 资源编码
     * @param dimensionId 维度编码
     * @param daRelation 与负责人关系
     * @param tablePrefix 表别名
     * @param addOwnSql 是否拼接自己的SQL
     * @return
     */
    @GetMapping({"/resource/code/sql"})
    RestResponse getDataPermissionSQLByUserIdAndResourceCode(@RequestParam("userId") String userId,
                                                             @RequestParam("resourceCode") String resourceCode,
                                                             @RequestParam("dimensionId") String dimensionId,
                                                             @RequestParam(value = "daRelation", required = false, defaultValue = "2") String daRelation,
                                                             @RequestParam(value = "tablePrefix", required = false) String tablePrefix,
                                                             @RequestParam(value = "addOwnSql", required = false) Boolean addOwnSql);
}
