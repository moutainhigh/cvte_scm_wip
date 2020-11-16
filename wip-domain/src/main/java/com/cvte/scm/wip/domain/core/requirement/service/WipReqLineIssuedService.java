package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/11/13 10:40
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipReqLineIssuedService {

    private QueryReqLineService queryReqLineService;
    private ReqLineRepository reqLineRepository;

    public WipReqLineIssuedService(QueryReqLineService queryReqLineService, ReqLineRepository reqLineRepository) {
        this.queryReqLineService = queryReqLineService;
        this.reqLineRepository = reqLineRepository;
    }

    public void issue(List<WipReqItemVO> reqItemVOList) {
        List<WipReqLineEntity> issuedReqLineList = new ArrayList<>();
        for (WipReqItemVO reqItemVO : reqItemVOList) {
            WipReqLineKeyQueryVO reqLineKeyQueryVO = new WipReqLineKeyQueryVO();
            reqLineKeyQueryVO.setOrganizationId(reqItemVO.getOrganizationId())
                    .setHeaderId(reqItemVO.getMoId())
                    .setItemId(reqItemVO.getItemId())
                    .setItemNo(reqItemVO.getItemNo())
                    .setWkpNo(reqItemVO.getWkpNo());
            List<WipReqLineEntity> reqLineList = queryReqLineService.getValidLine(reqLineKeyQueryVO);
            if (ListUtil.empty(reqLineList)) {
                throw new ParamsIncorrectException("更新发料数量失败，找不到投料行");
            }

            WipReqLineEntity randomLine = reqLineList.get(0);
            long itemIssuedQty = Optional.ofNullable(randomLine.getIssuedQty()).orElse(0L);
            long postQty = reqItemVO.getIssuedQty().longValue();
            itemIssuedQty += postQty;
            randomLine.setIssuedQty(itemIssuedQty);
            if (itemIssuedQty > 0 && !BillStatusEnum.ISSUED.getCode().equals(randomLine.getLineStatus())) {
                randomLine.setLineStatus(BillStatusEnum.ISSUED.getCode());
            }
            if (itemIssuedQty <= 0 && !BillStatusEnum.PREPARED.getCode().equals(randomLine.getLineStatus())) {
                randomLine.setLineStatus(BillStatusEnum.PREPARED.getCode());
            }
            issuedReqLineList.add(randomLine);
        }
        reqLineRepository.updateList(issuedReqLineList);
    }

}
