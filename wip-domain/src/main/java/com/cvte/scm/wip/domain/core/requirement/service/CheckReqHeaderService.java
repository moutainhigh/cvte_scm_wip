package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
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
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/8/19 17:11
 */
@Service
public class CheckReqHeaderService implements DomainService {

    private WipReqHeaderRepository wipReqHeaderRepository;

    public CheckReqHeaderService(WipReqHeaderRepository wipReqHeaderRepository) {
        this.wipReqHeaderRepository = wipReqHeaderRepository;
    }

    public String[] checkMoFinished(List<String> headerIdList) {
        if (ListUtil.empty(headerIdList)) {
            return new String[]{""};
        }
        List<String> distinctHeaderIdList = headerIdList.stream().distinct().collect(Collectors.toList());
        QueryWipReqHeaderVO queryWipReqHeaderVO = new QueryWipReqHeaderVO();
        queryWipReqHeaderVO.setWipHeaderIds(distinctHeaderIdList);
        List<WipReqHeaderEntity> headerEntityList = wipReqHeaderRepository.listWipReqHeaderEntity(queryWipReqHeaderVO);
        // 移除已发放的单据
        String[] errorMessages;
        headerEntityList.removeIf(header -> MoStatusTypeEnum.ISSUED.getCode().equals(header.getStatusType()));
        if (ListUtil.empty(headerEntityList)) {
            errorMessages = new String[]{""};
        } else {
            // 有状态非"已方法"的单据, 生成错误信息
            errorMessages = headerEntityList.stream().map(header -> {
                MoStatusTypeEnum statusTypeEnum = CodeableEnumUtils.getCodeableEnumByCode(header.getStatusType(), MoStatusTypeEnum.class);
                String statusType = statusTypeEnum == null ? "未知" : statusTypeEnum.getDesc();
                return String.format("%s的状态为\"%s\",只能更改已发放的单据", header.getSourceLotNo(), statusType);
            }).toArray(String[]::new);
        }
        // 接口表的处理使用了返回信息, 因此数量不足时需要补全
        int length = errorMessages.length;
        int size = headerIdList.size();
        if (length < size) {
            String[] temp = errorMessages;
            errorMessages = new String[size];
            for (int i = 0; i < size; i++) {
                if (i < length) {
                    errorMessages[i] = temp[i];
                } else {
                    errorMessages[i] = temp[length - 1];
                }
            }
        }
        return errorMessages;
    }

}
