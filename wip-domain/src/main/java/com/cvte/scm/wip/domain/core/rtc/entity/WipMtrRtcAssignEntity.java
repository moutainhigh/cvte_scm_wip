package com.cvte.scm.wip.domain.core.rtc.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcAssignRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 领退料单分配
 *
 * @author xueyuting
 * @since 2020-09-08
 */
@Data
@Component
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WipMtrRtcAssignEntity extends BaseModel implements Entity<String> {

    private WipMtrRtcAssignRepository wipMtrRtcAssignRepository;

    public WipMtrRtcAssignEntity() {}

    @Autowired
    public WipMtrRtcAssignEntity(WipMtrRtcAssignRepository wipMtrRtcAssignRepository) {
        this.wipMtrRtcAssignRepository = wipMtrRtcAssignRepository;
    }

    @Override
    public String getUniqueId() {
        return assignId;
    }

    private String assignId;

    private String organizationId;

    private String headerId;

    private String lineId;

    private String mtrLotNo;

    private String factoryId;

    private BigDecimal assignQty;

    private String invpNo;

    private String sourceOrderId;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

    public static WipMtrRtcAssignEntity get() {
        return DomainFactory.get(WipMtrRtcAssignEntity.class);
    }

}
