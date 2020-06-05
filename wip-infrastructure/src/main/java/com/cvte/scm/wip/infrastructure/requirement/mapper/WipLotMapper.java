package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;

import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-06-02
 */
public interface WipLotMapper extends CommonMapper<WipLotVO> {

    List<WipLotVO> selectByHeaderId(Integer headerId);

}