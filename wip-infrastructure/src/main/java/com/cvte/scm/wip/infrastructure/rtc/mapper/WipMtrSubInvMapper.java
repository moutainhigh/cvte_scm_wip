package com.cvte.scm.wip.infrastructure.rtc.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 12:07
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipMtrSubInvMapper extends CommonMapper<WipMtrSubInvVO> {

    List<WipMtrSubInvVO> selectByVO(@Param("subInvVOList") List<WipMtrSubInvVO> subInvVOList);
    /**
     * 强管控批次
     * @since 2020/9/21 4:35 下午
     * @author xueyuting
     */
    List<WipMtrSubInvVO> selectLotControl(@Param("organizationId") String organizationId,
                                          @Param("factoryId") String factoryId,
                                          @Param("itemId") String itemId,
                                          @Param("subinventoryCode") String subinventoryCode,
                                          @Param("moId") String moId);

}
