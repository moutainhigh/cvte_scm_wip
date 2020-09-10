package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Predicate;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/10 16:55
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class CheckMtrRtcLineService {

    public void checkQtyLower(WipMtrRtcLineBuildVO rtcLineBuildVO) {
        if (rtcLineBuildVO.getReqQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParamsIncorrectException("数量必须大于0");
        }
    }

    public void roundQty(WipMtrRtcLineBuildVO rtcLineBuildVO) {
        Predicate<BigDecimal> isInteger = v -> v.scale() <= 0;
        BigDecimal reqQty = rtcLineBuildVO.getReqQty();
        if (Objects.nonNull(reqQty) && !isInteger.test(reqQty)) {
            rtcLineBuildVO.setReqQty(reqQty.setScale(0, RoundingMode.HALF_UP));
        }
        BigDecimal issuedQty = rtcLineBuildVO.getIssuedQty();
        if (Objects.nonNull(issuedQty) && !isInteger.test(issuedQty)) {
            rtcLineBuildVO.setIssuedQty(issuedQty.setScale(0, RoundingMode.HALF_UP));
        }
    }

    public void checkIssuedQty(WipMtrRtcLineBuildVO rtcLineBuildVO, WipMtrRtcLineEntity rtcLineEntity) {
        BigDecimal issuedQty = rtcLineBuildVO.getIssuedQty();
        BigDecimal reqQty = rtcLineBuildVO.getReqQty();
        if (Objects.nonNull(issuedQty)) {
            if (Objects.isNull(reqQty)) {
                reqQty = rtcLineEntity.getReqQty();
            }
            if (issuedQty.compareTo(reqQty) > 0) {
                throw new ParamsIncorrectException("实发数量不可超过申请数量");
            }
        }
    }

    public void checkQtyUpper(BigDecimal qty, WipReqItemVO reqItemVO, String billType) {
        // 领料or负数物料需求的退料 -- 申请量<=投料单未完成量-有效申请领料单量
        BigDecimal availableQty = reqItemVO.getUnIssuedQty().abs().subtract(reqItemVO.getUnPostQty());
        String errMsg;
        if (WipMtrRtcHeaderTypeEnum.RECEIVE.getCode().equals(billType)) {
            errMsg = "领料数量只能小于可领数量";
        } else {
            // 退料
            errMsg = "退料数量只能小于可退数量";
            if (reqItemVO.getReqQty().compareTo(BigDecimal.ZERO) > 0) {
                // 正数物料需求的退料 -- 申请量<=工单已领数量-已申请数量之和
                availableQty = reqItemVO.getIssuedQty().subtract(reqItemVO.getUnPostQty());
            }
        }
        if (qty.compareTo(availableQty) > 0) {
            throw new ParamsIncorrectException(errMsg + availableQty.toString());
        }
    }

}
