package com.cvte.scm.wip.infrastructure.sys.bu.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.sys.bu.mapper.dataobject.SysBuDeptDO;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/28 13:13
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface SysBuDeptMapper extends CommonMapper<SysBuDeptDO> {
    /**
     * 根据EBS订单类型编码获取事业部
     * @since 2020/4/10 4:59 下午
     * @author xueyuting
     * @param typeCode 订单类型
     */
    SysBuDeptDO selectBuDeptByTypeCode(String typeCode);
}
