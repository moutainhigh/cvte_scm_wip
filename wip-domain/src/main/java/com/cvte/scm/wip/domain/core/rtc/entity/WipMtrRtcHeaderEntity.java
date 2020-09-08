package com.cvte.scm.wip.domain.core.rtc.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcHeaderRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 领退料单
 *
 * @author xueyuting
 * @since 2020-09-08
 */
@Data
@Component
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WipMtrRtcHeaderEntity extends BaseModel implements Entity<String> {

    private WipMtrRtcHeaderRepository wipMtrRtcHeaderRepository;

    public WipMtrRtcHeaderEntity(WipMtrRtcHeaderRepository wipMtrRtcHeaderRepository) {
        this.wipMtrRtcHeaderRepository = wipMtrRtcHeaderRepository;
    }

    @Override
    public String getUniqueId() {
        return headerId;
    }

    private String headerId;

    private String organizationId;

    private String billNo;

    private String billType;

    private String moId;

    private String wkpNo;

    private String deptNo;

    private String factoryId;

    private String remark;

    private BigDecimal billQty;

    private String invpNo;

    private String billStatus;

    private String sourceBillNo;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

    public static WipMtrRtcHeaderEntity get() {
        return DomainFactory.get(WipMtrRtcHeaderEntity.class);
    }

}
