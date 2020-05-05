package com.cvte.scm.wip.infrastructure.client.sys.base;

import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.OrgRelationBaseDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 16:01
 */
@FeignClient("scm-bsm")
@RequestMapping("/api/org/switch")
public interface MultiOrgSwitchApiClient {

    /**
     * 传入指定节点，递归遍历获取父节点为ORG_TYPE的节点
     * @param id 组织节点编码
     * @param orgType 父节点的节点类型
     * @return
     */
    @GetMapping("/parent/{id}")
    FeignResult<OrgRelationBaseDTO> getParentOrgRelationByType(@PathVariable("id") String id,
                                                               @RequestParam(value = "orgType", required = false) String orgType);
}
