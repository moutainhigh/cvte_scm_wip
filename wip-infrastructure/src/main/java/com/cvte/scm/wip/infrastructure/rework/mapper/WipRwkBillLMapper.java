package com.cvte.scm.wip.infrastructure.rework.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkBillLVO;
import com.cvte.scm.wip.infrastructure.rework.mapper.dataobject.WipRwkBillLDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-03-23
 */
public interface WipRwkBillLMapper extends CommonMapper<WipRwkBillLDO> {

    List<WipRwkBillLVO> sumRwkQty(@Param("factoryIdList") List<String> factoryIdList, @Param("moLotNoList") List<String> moLotNoList);

}