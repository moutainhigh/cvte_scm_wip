package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/9 11:26
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class CheckMtrRtcHeaderService {

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

}
