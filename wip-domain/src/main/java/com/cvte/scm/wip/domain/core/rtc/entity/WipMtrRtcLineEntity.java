package com.cvte.scm.wip.domain.core.rtc.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

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

    public WipMtrRtcLineEntity() {}

    @Autowired
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

    public List<WipMtrRtcLineEntity> getByHeaderId(String headerId) {
        List<WipMtrRtcLineEntity> lineList = wipMtrRtcLineRepository.selectByHeaderId(headerId);
        lineList.forEach(this::wiredAfterSelect);
        return lineList;
    }

    public List<WipMtrRtcLineEntity> getByLineIds(String[] lineIdArr) {
        List<WipMtrRtcLineEntity> lineList = wipMtrRtcLineRepository.selectListByIds(lineIdArr);
        lineList.forEach(this::wiredAfterSelect);
        return lineList;
    }

    BigDecimal calculateQty(WipMtrRtcHeaderBuildVO rtcHeaderBuildVO, WipReqItemVO reqItemVO) {
        // 领料套数
        BigDecimal billQty = rtcHeaderBuildVO.getBillQty();
        // 有效申请未过账数
        BigDecimal unPostQty = reqItemVO.getUnPostQty();
        if (WipMtrRtcHeaderTypeEnum.RECEIVE.getCode().equals(rtcHeaderBuildVO.getBillType())) {
            // 领料单行 : min( 领料套数*单位用量/BOM产出率, 工单未领数量-有效申请未过账之和)
            BigDecimal totalQty = rtcHeaderBuildVO.getBillQty().multiply(reqItemVO.getUnitQty()).divide(reqItemVO.getComponentYieldFactor(), RoundingMode.HALF_UP);
            BigDecimal availableQty = reqItemVO.getUnIssuedQty().subtract(unPostQty);
            return totalQty.min(availableQty);
        } else {
            // 退料单行
            BigDecimal itemReqQty = reqItemVO.getReqQty();
            BigDecimal completeQty = rtcHeaderBuildVO.getCompleteQty().multiply(reqItemVO.getUnitQty());
            BigDecimal unCompleteQty = rtcHeaderBuildVO.getUnCompleteQty().multiply(reqItemVO.getUnitQty());
            BigDecimal availableQty;
            if (itemReqQty.compareTo(BigDecimal.ZERO) > 0) {
                // min(未完工数*单位用量，物料已领数量-工单完工数量*单位用量-已做退料单未过账之和，退料套数*单位用量)
                availableQty = reqItemVO.getIssuedQty().subtract(completeQty).subtract(unPostQty);
                BigDecimal totalQty = billQty.multiply(reqItemVO.getUnitQty());
                return unCompleteQty.min(availableQty).min(totalQty);
            } else if (itemReqQty.compareTo(BigDecimal.ZERO) < 0) {
                // min(未完工数*单位用量，退料申请数=需求数-已领料数-已创建退料单未过账数)
                return reqItemVO.getReqQty().subtract(reqItemVO.getIssuedQty()).subtract(unPostQty);
            } else {
                // 用量为0
                BigDecimal reqItemIssuedQty = reqItemVO.getIssuedQty();
                if (Objects.nonNull(reqItemIssuedQty) && reqItemIssuedQty.compareTo(BigDecimal.ZERO) > 0) {
                    // 发料>0时，退料申请数=已领料数量-已创建退料单未过账数
                    return reqItemIssuedQty.subtract(unPostQty);
                } else {
                    // 当未发料时，不允许退料
                    return BigDecimal.ZERO;
                }
            }
        }
    }

    public void update(WipMtrRtcLineBuildVO rtcLineBuildVO) {
        BiPredicate<Object, Object> valueChanged = (v, p) -> Objects.nonNull(v) && !v.equals(p);
        if (valueChanged.test(rtcLineBuildVO.getReqQty(), this.reqQty)) {
            this.setReqQty(rtcLineBuildVO.getReqQty());
        }
        if (valueChanged.test(rtcLineBuildVO.getInvpNo(), this.invpNo)) {
            this.setInvpNo(rtcLineBuildVO.getInvpNo());
        }
        if (valueChanged.test(rtcLineBuildVO.getIssuedQty(), this.issuedQty)) {
            this.setIssuedQty(rtcLineBuildVO.getIssuedQty());
        }
        if (valueChanged.test(rtcLineBuildVO.getSupplier(), this.supplier)) {
            this.setSupplier(rtcLineBuildVO.getSupplier());
        }
        if (valueChanged.test(rtcLineBuildVO.getSerialNo(), this.serialNo)) {
            this.setSerialNo(rtcLineBuildVO.getSerialNo());
        }
        if (valueChanged.test(rtcLineBuildVO.getBadReason(), this.badMtrReason)) {
            this.setBadMtrReason(rtcLineBuildVO.getBadReason());
        }
        if (valueChanged.test(rtcLineBuildVO.getBadDesc(), this.badMtrDesc)) {
            this.setBadMtrDesc(rtcLineBuildVO.getBadDesc());
        }
    }

    public String getReqKey(String moId) {
        return String.join("_", this.organizationId, moId, this.itemId, this.wkpNo);
    }

    private void wiredAfterSelect(WipMtrRtcLineEntity rtcLineEntity) {
        rtcLineEntity.setWipMtrRtcLineRepository(this.getWipMtrRtcLineRepository());
    }

    public static WipMtrRtcLineEntity get() {
        return DomainFactory.get(WipMtrRtcLineEntity.class);
    }

}
