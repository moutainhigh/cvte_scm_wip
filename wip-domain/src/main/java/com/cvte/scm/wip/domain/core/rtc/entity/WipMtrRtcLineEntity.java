package com.cvte.scm.wip.domain.core.rtc.entity;

import com.cvte.csb.toolkit.ArrayUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcAssignRepository;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
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
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

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
    private WipMtrRtcAssignRepository wipMtrRtcAssignRepository;

    public WipMtrRtcLineEntity() {}

    @Autowired
    public WipMtrRtcLineEntity(WipMtrRtcLineRepository wipMtrRtcLineRepository, WipMtrRtcAssignRepository wipMtrRtcAssignRepository) {
        this.wipMtrRtcLineRepository = wipMtrRtcLineRepository;
        this.wipMtrRtcAssignRepository = wipMtrRtcAssignRepository;
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

    private List<WipMtrRtcAssignEntity> assignList;

    public List<WipMtrRtcAssignEntity> getAssignList() {
        if (ListUtil.notEmpty(this.assignList)) {
            return this.assignList;
        }
        if (StringUtils.isBlank(this.lineId)) {
            return new ArrayList<>();
        }
        this.assignList = WipMtrRtcAssignEntity.get().getByLineIds(Collections.singletonList(this.lineId));
        return this.assignList;
    }

    public List<WipMtrRtcLineEntity> getByHeaderId(String headerId) {
        List<WipMtrRtcLineEntity> lineList = wipMtrRtcLineRepository.selectByHeaderId(headerId);
        lineList.forEach(this::wiredAfterSelect);
        return lineList;
    }

    public List<WipMtrRtcLineEntity> getByLineIds(String[] lineIdArr) {
        if (ArrayUtils.isEmpty(lineIdArr)) {
            return new ArrayList<>();
        }
        List<WipMtrRtcLineEntity> lineList = wipMtrRtcLineRepository.selectListByIds(lineIdArr);
        lineList.forEach(this::wiredAfterSelect);
        return lineList;
    }

    public void batchGetAssign(List<WipMtrRtcLineEntity> rtcLineEntityList) {
        List<String> lineIdList = rtcLineEntityList.stream().map(WipMtrRtcLineEntity::getLineId).collect(Collectors.toList());
        List<WipMtrRtcAssignEntity> rtcAssignEntityList = WipMtrRtcAssignEntity.get().getByLineIds(lineIdList);
        if (ListUtil.empty(rtcAssignEntityList)) {
            return;
        }
        Map<String, List<WipMtrRtcAssignEntity>> lineAssignMap = rtcAssignEntityList.stream().collect(Collectors.groupingBy(WipMtrRtcAssignEntity::getLineId));
        for (WipMtrRtcLineEntity rtcLineEntity : rtcLineEntityList) {
            List<WipMtrRtcAssignEntity> lineAssignList = lineAssignMap.get(rtcLineEntity.getLineId());
            if (ListUtil.notEmpty(lineAssignList)) {
                rtcLineEntity.setAssignList(lineAssignList);
            }
        }
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
        if (valueChanged.test(rtcLineBuildVO.getBadMtrReason(), this.badMtrReason)) {
            this.setBadMtrReason(rtcLineBuildVO.getBadMtrReason());
        }
        if (valueChanged.test(rtcLineBuildVO.getBadMtrDesc(), this.badMtrDesc)) {
            this.setBadMtrDesc(rtcLineBuildVO.getBadMtrDesc());
        }
    }

    public void createAssigns(List<WipMtrRtcAssignEntity> rtcAssignEntityList) {
        rtcAssignEntityList.forEach(assign -> EntityUtils.writeStdCrtInfoToEntity(assign, EntityUtils.getWipUserId()));
        wipMtrRtcAssignRepository.insertList(rtcAssignEntityList);
    }

    public void updateAssigns(List<WipMtrRtcAssignEntity> rtcAssignEntityList) {
        rtcAssignEntityList.forEach(assign -> EntityUtils.writeStdUpdInfoToEntity(assign, EntityUtils.getWipUserId()));
        wipMtrRtcAssignRepository.updateList(rtcAssignEntityList);
    }

    public void deleteAssigns(List<WipMtrRtcAssignEntity> rtcAssignEntityList) {
        for (WipMtrRtcAssignEntity deleteAssignEntity : rtcAssignEntityList) {
            deleteAssignEntity.setAssignStatus(StatusEnum.CLOSE.getCode());
            EntityUtils.writeStdUpdInfoToEntity(deleteAssignEntity, EntityUtils.getWipUserId());
        }
        wipMtrRtcAssignRepository.updateList(rtcAssignEntityList);
    }

    public String getReqKey(String moId) {
        return String.join("_", this.organizationId, moId, this.itemId, this.wkpNo);
    }

    private void wiredAfterSelect(WipMtrRtcLineEntity rtcLineEntity) {
        rtcLineEntity.setWipMtrRtcLineRepository(this.wipMtrRtcLineRepository);
        rtcLineEntity.setWipMtrRtcAssignRepository(this.wipMtrRtcAssignRepository);
    }

    public static WipMtrRtcLineEntity get() {
        return DomainFactory.get(WipMtrRtcLineEntity.class);
    }

}
