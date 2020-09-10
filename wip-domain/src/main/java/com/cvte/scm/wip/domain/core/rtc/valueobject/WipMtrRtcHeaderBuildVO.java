package com.cvte.scm.wip.domain.core.rtc.valueobject;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/8 17:39
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrRtcHeaderBuildVO implements VO {

    private String headerId;

    private String organizationId;

    private String moNo;

    private String wkpNo;

    private String billType;

    private BigDecimal billQty;

    private String invpNo;

    private String deptNo;

    private String remark;

    private String sourceBillNo;

    private List<String> itemList;

    private String factoryId;

    private String moId;

    private BigDecimal originQty;

    private BigDecimal completeQty;

    private BigDecimal unCompleteQty;

    /**
     * 填充工单信息
     * @since 2020/9/9 10:15 上午
     * @author xueyuting
     */
    public void fillMoInfo(WipReqHeaderEntity headerEntity) {
        if (Objects.isNull(headerEntity) || StringUtils.isBlank(headerEntity.getSourceId())) {
            throw new ParamsIncorrectException("工单不存在");
        }
        if (StringUtils.isNotBlank(this.moNo) && StringUtils.isBlank(this.moId)) {
            // 设置工单ID
            this.setMoId(headerEntity.getSourceId());
        }
        if (StringUtils.isBlank(this.factoryId)) {
            // 设置工厂ID
            this.setFactoryId(headerEntity.getFactoryId());
        }
        BigDecimal reqBillQty = BigDecimal.valueOf(headerEntity.getBillQty());
        BigDecimal reqCompleteQty = Optional.ofNullable(headerEntity.getCompleteQty()).orElse(BigDecimal.ZERO);
        this.setOriginQty(reqBillQty);
        this.setCompleteQty(reqCompleteQty);
        this.setUnCompleteQty(reqBillQty.subtract(reqCompleteQty));
    }

}
