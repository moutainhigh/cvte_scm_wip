package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqMtrsRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/3 15:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class QueryReqLineService implements DomainService {

    private WipReqLineRepository lineRepository;
    private WipReqMtrsRepository wipReqMtrsRepository;

    public QueryReqLineService(WipReqLineRepository lineRepository, WipReqMtrsRepository wipReqMtrsRepository) {
        this.lineRepository = lineRepository;
        this.wipReqMtrsRepository = wipReqMtrsRepository;
    }

    public List<WipReqLineEntity> getValidLine(WipReqLineKeyQueryVO keyQueryVO) {
        Set<BillStatusEnum> statusList = BillStatusEnum.getValidStatusSet();
        return lineRepository.selectValidByKey(keyQueryVO, statusList);
    }

    public List<WipReqLineEntity> getReqLinesByMtrsItem(WipReqLineKeyQueryVO keyQueryVO) {
        // 获取替代料
        List<String> mtrsItemNoList = wipReqMtrsRepository.selectMtrsItemNo(keyQueryVO.getHeaderId(), keyQueryVO.getItemNo());
        // 获取联络函替代料
        List<String> subRuleMtrsItemNoList = wipReqMtrsRepository.selectSubRuleMtrsItemNo(keyQueryVO.getHeaderId(), keyQueryVO.getItemId());
        // 替代料合并
        Set<String> totalMtrsItemNoList = new HashSet<>();
        totalMtrsItemNoList.addAll(mtrsItemNoList);
        totalMtrsItemNoList.addAll(subRuleMtrsItemNoList);

        List<WipReqLineEntity> mtrsLineList = new ArrayList<>();
        WipReqLineKeyQueryVO localVO = new WipReqLineKeyQueryVO();
        BeanUtils.copyProperties(keyQueryVO, localVO);
        localVO.setItemId(null);
        for (String mtrsItemNo : totalMtrsItemNoList) {
            localVO.setItemNo(mtrsItemNo);
            mtrsLineList.addAll(this.getValidLine(localVO));
        }

        return mtrsLineList;
    }

}
