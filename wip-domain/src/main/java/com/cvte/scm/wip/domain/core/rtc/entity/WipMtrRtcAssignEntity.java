package com.cvte.scm.wip.domain.core.rtc.entity;

import cn.hutool.core.collection.CollectionUtil;
import com.cvte.csb.toolkit.ArrayUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcAssignRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcAssignBuildVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiPredicate;

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

    private String lineId;

    private String headerId;

    private String organizationId;

    private String factoryId;

    private String invpNo;

    private String mtrLotNo;

    private BigDecimal assignQty;

    private BigDecimal issuedQty;

    private String lotControlType;

    private String sourceOrderId;

    private String assignStatus;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

    public List<WipMtrRtcAssignEntity> getByIds(String[] assignIdArr) {
        if (ArrayUtils.isEmpty(assignIdArr)) {
            return new ArrayList<>();
        }
        List<WipMtrRtcAssignEntity> rtcAssignEntityList = wipMtrRtcAssignRepository.selectListByIds(assignIdArr);
        rtcAssignEntityList.forEach(this::wiredAfterSelect);
        return rtcAssignEntityList;
    }

    public List<WipMtrRtcAssignEntity> getByLineIds(Collection<String> lineIdList) {
        if (CollectionUtil.isEmpty(lineIdList)) {
            return new ArrayList<>();
        }
        return wipMtrRtcAssignRepository.selectByLineIds(lineIdList);
    }

    public void create(WipMtrRtcAssignBuildVO assignBuildVO) {
        this.setAssignId(UUIDUtils.get32UUID())
                .setLineId(assignBuildVO.getLineId())
                .setHeaderId(assignBuildVO.getHeaderId())
                .setOrganizationId(assignBuildVO.getOrganizationId())
                .setFactoryId(assignBuildVO.getFactoryId())
                .setInvpNo(assignBuildVO.getInvpNo())
                .setMtrLotNo(assignBuildVO.getMtrLotNo())
                .setAssignQty(assignBuildVO.getAssignQty())
                .setIssuedQty(assignBuildVO.getIssuedQty())
                .setAssignStatus(StatusEnum.NORMAL.getCode())
                .setLotControlType(assignBuildVO.getLotControlType());
    }

    public void update(WipMtrRtcAssignBuildVO assignBuildVO) {
        BiPredicate<Object, Object> valueChanged = (v, p) -> Objects.nonNull(v) && !v.equals(p);
        if (valueChanged.test(assignBuildVO.getInvpNo(), this.invpNo)) {
            this.setInvpNo(assignBuildVO.getInvpNo());
        }
        if (valueChanged.test(assignBuildVO.getMtrLotNo(), this.mtrLotNo)) {
            this.setMtrLotNo(assignBuildVO.getMtrLotNo());
        }
        if (valueChanged.test(assignBuildVO.getAssignQty(), this.assignQty)) {
            this.setAssignQty(assignBuildVO.getAssignQty());
        }
        if (valueChanged.test(assignBuildVO.getIssuedQty(), this.issuedQty)) {
            this.setIssuedQty(assignBuildVO.getIssuedQty());
        }
        if (valueChanged.test(assignBuildVO.getLotControlType(), this.lotControlType)) {
            this.setLotControlType(assignBuildVO.getLotControlType());
        }
    }

    private void wiredAfterSelect(WipMtrRtcAssignEntity rtcAssignEntity) {
        rtcAssignEntity.setWipMtrRtcAssignRepository(this.wipMtrRtcAssignRepository);
    }

    public static WipMtrRtcAssignEntity get() {
        return DomainFactory.get(WipMtrRtcAssignEntity.class);
    }

}
