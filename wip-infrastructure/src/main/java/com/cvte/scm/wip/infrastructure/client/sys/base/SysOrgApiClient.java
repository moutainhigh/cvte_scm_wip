package com.cvte.scm.wip.infrastructure.client.sys.base;

import com.cvte.csb.sys.base.entity.SysOrgDimension;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.OrgRelationBaseDTO;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.SysOrgExt;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 11:48
 */
@FeignClient("scm-bsm")
@RequestMapping("/admin/v1/org")
public interface SysOrgApiClient {

    /**
     * 根据编码获取组织节点
     * @param id 组织节点编码
     * @return
     */
    @GetMapping({"/relation_tree/{id}"})
    FeignResult<OrgRelationBaseDTO> getOrgRelationById(@PathVariable("id") String id);


    /**
     * 获取组织拓展属性
     * @param id
     * @return
     */
    @GetMapping({"/{id}/ext"})
    FeignResult<SysOrgExt> getOrgExtById(@PathVariable("id") String id);

    /**
     * 获取系统所有维度
     * @param
     * @return
     */
    @GetMapping("/dimension")
    FeignResult<List<SysOrgDimension>> getOrgDimension();

}
