package com.cvte.scm.wip.domain.core.thirdpart.ebs.service;


import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockDTO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockResponse;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockVO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.query.EbsInoutStockQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-11 09:43
 **/
@Service
public interface EbsInvokeService {

    /**
     * 查询调拨数据
     *
     * @param
     * @param query
     * @return java.util.List<com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.EbsInoutStockView>
     **/
    List<EbsInoutStockVO> listEbsInoutStockView(EbsInoutStockQuery query);

    /**
     * 创建调拨单
     *
     * @param
     * @param ebsInoutStockDTO
     * @return com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.EbsInoutStockResponse
     **/
    EbsInoutStockResponse inoutStockImport(EbsInoutStockDTO ebsInoutStockDTO);

}
