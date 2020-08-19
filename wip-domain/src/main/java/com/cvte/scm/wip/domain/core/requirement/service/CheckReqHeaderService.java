package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.QueryWipReqHeaderVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.MoStatusTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/19 17:11
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class CheckReqHeaderService implements DomainService {

    private WipReqHeaderRepository wipReqHeaderRepository;

    public CheckReqHeaderService(WipReqHeaderRepository wipReqHeaderRepository) {
        this.wipReqHeaderRepository = wipReqHeaderRepository;
    }

    public String[] checkMoFinished(List<String> headerIdList) {
        List<String> distinctHeaderIdList = headerIdList.stream().distinct().collect(Collectors.toList());
        QueryWipReqHeaderVO queryWipReqHeaderVO = new QueryWipReqHeaderVO();
        queryWipReqHeaderVO.setWipHeaderIds(distinctHeaderIdList);
        List<WipReqHeaderEntity> headerEntityList = wipReqHeaderRepository.listWipReqHeaderEntity(queryWipReqHeaderVO);
        return headerEntityList.stream()
                .filter(header -> StringUtils.isNotBlank(header.getStatusType()) && !MoStatusTypeEnum.ISSUED.getCode().equals(header.getStatusType()))
                .map(header -> {
                    MoStatusTypeEnum statusTypeEnum = CodeableEnumUtils.getCodeableEnumByCode(header.getStatusType(), MoStatusTypeEnum.class);
                    String statusType = statusTypeEnum == null ? "未知" : statusTypeEnum.getDesc();
                    return String.format("%s的状态为\"%s\",只能更改已发放的单据", header.getSourceLotNo(), statusType);
                })
                .toArray(String[]::new);
    }

}
