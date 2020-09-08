package com.cvte.scm.wip.domain.core.rtc.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 领退料单行
 *
 * @author xueyuting
 * @since 2020-09-08
 */
@Data
@Component
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WipMtrRtcLineEntity extends BaseModel implements Entity<String> {

    private WipMtrRtcLineRepository wipMtrRtcLineRepository;

    public WipMtrRtcLineEntity(WipMtrRtcLineRepository wipMtrRtcLineRepository) {
        this.wipMtrRtcLineRepository = wipMtrRtcLineRepository;
    }

    @Override
    public String getUniqueId() {
        return lineId;
    }

    private String lineId;

    private String headerId;

    private String organizationId;

    private String itemId;

    private String itemNo;

    private String moLotNo;

    private String wkpNo;

    private String invpNo;

    private BigDecimal reqQty;

    private BigDecimal issuedQty;

    private String lineStatus;

    private Date postDate;

    private String supplier;

    private String serialNo;

    private String processResult;

    private String badMtrReason;

    private String badMtrDesc;

    private String remark;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

    public static WipMtrRtcLineEntity get() {
        return DomainFactory.get(WipMtrRtcLineEntity.class);
    }

}
