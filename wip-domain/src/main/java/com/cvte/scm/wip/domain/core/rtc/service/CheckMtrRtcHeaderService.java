package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcHeaderRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/9 11:26
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class CheckMtrRtcHeaderService {

    private WipMtrRtcHeaderRepository wipMtrRtcHeaderRepository;

    public CheckMtrRtcHeaderService(WipMtrRtcHeaderRepository wipMtrRtcHeaderRepository) {
        this.wipMtrRtcHeaderRepository = wipMtrRtcHeaderRepository;
    }

    public void checkBillStatus(String status) {
        BillStatusEnum statusEnum = CodeableEnumUtils.getCodeableEnumByCode(status, BillStatusEnum.class);
        if (Objects.isNull(statusEnum)) {
            throw new ParamsIncorrectException("非法工单状态");
        }
        switch (statusEnum) {
            case CLOSED:
            case CANCELLED:
            case FINISH:
                throw new ParamsIncorrectException("工单" + statusEnum.getDesc());
            default:
        }
    }

    public void checkBillQtyLower(BigDecimal billQty) {
        if (billQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParamsIncorrectException("领料套数必须大于0");
        }
    }

    public void checkBillQtyUpper(BigDecimal billQty, BigDecimal originQty) {
        if (Objects.nonNull(billQty) && billQty.compareTo(originQty) > 0) {
            throw new ParamsIncorrectException("领料套数不能超过工单数量");
        }
    }

    public void checkReqFinished(WipMtrRtcHeaderEntity rtcHeader) {
        if (CollectionUtils.isEmpty(rtcHeader.getLineList().stream().map(WipMtrRtcLineEntity::getLineStatus).filter(WipMtrRtcLineStatusEnum.getUnPostStatus()::contains).collect(Collectors.toSet()))) {
            // 有效行为空
            String errMsg;
            WipMtrRtcHeaderTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(rtcHeader.getBillType(), WipMtrRtcHeaderTypeEnum.class);

            List<WipMtrRtcHeaderEntity> unPostRtcHeaderList = wipMtrRtcHeaderRepository.selectUnPost(rtcHeader);
            if (ListUtil.notEmpty(unPostRtcHeaderList)) {
                String billNoStr = unPostRtcHeaderList.stream().map(WipMtrRtcHeaderEntity::getBillNo).collect(Collectors.joining(","));
                errMsg = "工单没有物料可%s,存在未过账的单据:" + billNoStr;
            } else {
                if (StringUtils.isNotBlank(rtcHeader.getWkpNo())) {
                    errMsg = "所选工序物料已%s完成";
                } else {
                    errMsg = "工单物料已%s完成";
                }
            }
            errMsg = String.format(errMsg, Optional.ofNullable(typeEnum).orElse(WipMtrRtcHeaderTypeEnum.RECEIVE).getDesc());
            throw new ParamsIncorrectException(errMsg);
        }
    }

}
