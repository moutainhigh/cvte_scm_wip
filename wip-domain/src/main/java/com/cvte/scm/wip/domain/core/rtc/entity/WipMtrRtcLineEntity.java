package com.cvte.scm.wip.domain.core.rtc.entity;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.ArrayUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.audit.AuditEntity;
import com.cvte.scm.wip.common.audit.AuditField;
import com.cvte.scm.wip.common.audit.AuditId;
import com.cvte.scm.wip.common.audit.AuditParentId;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcAssignRepository;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderStatusEnum;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
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

import static com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum.*;

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
@AuditEntity(modelName = "rtc", entityName = "line")
public class WipMtrRtcLineEntity extends BaseModel implements Entity<String> {

    private WipMtrRtcLineRepository wipMtrRtcLineRepository;
    private WipMtrRtcAssignRepository wipMtrRtcAssignRepository;
    private WipReqLotIssuedRepository wipReqLotIssuedRepository;

    public WipMtrRtcLineEntity() {}

    @Autowired
    public WipMtrRtcLineEntity(WipMtrRtcLineRepository wipMtrRtcLineRepository, WipMtrRtcAssignRepository wipMtrRtcAssignRepository, WipReqLotIssuedRepository wipReqLotIssuedRepository) {
        this.wipMtrRtcLineRepository = wipMtrRtcLineRepository;
        this.wipMtrRtcAssignRepository = wipMtrRtcAssignRepository;
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
    }

    @Override
    public String getUniqueId() {
        return lineId;
    }

    @AuditId
    private String lineId;

    @AuditParentId
    private String headerId;

    private String organizationId;

    private String itemId;

    private String itemNo;

    private String moLotNo;

    private String wkpNo;

    @AuditField(fieldName = "子库")
    private String invpNo;

    @AuditField(fieldName = "需求数量")
    private BigDecimal reqQty;

    @AuditField(fieldName = "实发数量")
    private BigDecimal issuedQty;

    private String lineStatus;

    private Date postDate;

    private String supplier;

    private String serialNo;

    private String processResult;

    private String badMtrReason;

    private String badMtrDesc;

    @AuditField(fieldName = "备注")
    private String remark;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

    private String sourceLineId;

    private String supplierName;

    private List<WipMtrRtcAssignEntity> assignList;

    public List<WipMtrRtcAssignEntity> getAssignList() {
        if (Objects.nonNull(this.assignList)) {
            // 为空列表时不重复查询
            return this.assignList;
        }
        if (StringUtils.isBlank(this.lineId)) {
            return new ArrayList<>();
        }
        this.assignList = WipMtrRtcAssignEntity.get().getByLineIds(Collections.singletonList(this.lineId));
        return this.assignList;
    }

    public WipMtrRtcLineEntity getById(String lineId) {
        if (StringUtils.isBlank(lineId)) {
            throw new ParamsIncorrectException("单据行ID不可为空");
        }
        WipMtrRtcLineEntity selectEntity = wipMtrRtcLineRepository.selectById(lineId);
        this.wiredAfterSelect(selectEntity);
        return selectEntity;
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

    public void batchClose(List<WipMtrRtcLineEntity> closeLineList) {
        if (ListUtil.empty(closeLineList)) {
            return;
        }
        for (WipMtrRtcLineEntity closeLine : closeLineList) {
            if (POSTED.getCode().equals(closeLine.getLineStatus())) {
                throw new ParamsIncorrectException("已过账的单据无法关闭");
            }
            closeLine.setLineStatus(WipMtrRtcLineStatusEnum.CLOSED.getCode());
            EntityUtils.writeStdUpdInfoToEntity(closeLine, EntityUtils.getWipUserId());
        }
        this.deleteLineAssigns(closeLineList);
        wipMtrRtcLineRepository.updateList(closeLineList);
    }

    public void batchCancel(List<WipMtrRtcLineEntity> cancelLineList) {
        if (ListUtil.empty(cancelLineList)) {
            return;
        }
        Iterator<WipMtrRtcLineEntity> iterator = cancelLineList.iterator();
        while (iterator.hasNext()) {
            WipMtrRtcLineEntity rtcLine = iterator.next();
            if (getInvalidStatus().contains(rtcLine.getLineStatus())) {
                iterator.remove();
                continue;
            }
            rtcLine.setLineStatus(CANCELED.getCode());
            EntityUtils.writeStdUpdInfoToEntity(rtcLine, EntityUtils.getWipUserId());
        }
        this.deleteLineAssigns(cancelLineList);
        wipMtrRtcLineRepository.updateList(cancelLineList);
    }

    public void batchGetAssign(List<WipMtrRtcLineEntity> rtcLineList) {
        List<String> lineIdList = rtcLineList.stream().map(WipMtrRtcLineEntity::getLineId).collect(Collectors.toList());
        List<WipMtrRtcAssignEntity> rtcAssignList = WipMtrRtcAssignEntity.get().getByLineIds(lineIdList);
        if (Objects.isNull(rtcAssignList)) {
            rtcAssignList = Collections.emptyList();
        }
        Map<String, List<WipMtrRtcAssignEntity>> lineAssignMap = rtcAssignList.stream().collect(Collectors.groupingBy(WipMtrRtcAssignEntity::getLineId));
        for (WipMtrRtcLineEntity rtcLineEntity : rtcLineList) {
            List<WipMtrRtcAssignEntity> lineAssignList = lineAssignMap.get(rtcLineEntity.getLineId());
            if (Objects.nonNull(lineAssignList)) {
                rtcLineEntity.setAssignList(lineAssignList);
            } else {
                // 批次为空时设置为空列表
                rtcLineEntity.setAssignList(new ArrayList<>(0));
            }
        }
    }

    public void batchPost(List<WipMtrRtcLineEntity> rtcLineList) {
        for (WipMtrRtcLineEntity rtcLine : rtcLineList) {
            rtcLine.setLineStatus(POSTED.getCode());
            EntityUtils.writeStdUpdInfoToEntity(rtcLine, EntityUtils.getWipUserId());
        }
        wipMtrRtcLineRepository.updateList(rtcLineList);
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
                // min(未完工数*单位用量，退料申请数=需求数-已领料数-已创建退料单未过账数), 注:已创建退料单未过账数为正数,故用add
                BigDecimal applyQty = reqItemVO.getReqQty().subtract(reqItemVO.getIssuedQty()).add(unPostQty);
                // 算出来为负数, 取绝对值
                return unCompleteQty.abs().min(applyQty.abs());
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

    public void update() {
        EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
        wipMtrRtcLineRepository.updateSelectiveById(this);
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
            this.setSupplier(rtcLineBuildVO.getSupplier())
                    .setSupplierName(rtcLineBuildVO.getSupplierName());
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
        if (valueChanged.test(rtcLineBuildVO.getRemark(), this.remark)) {
            this.setRemark(rtcLineBuildVO.getRemark());
        }
    }

    public List<WipMtrRtcAssignEntity> generateAssign(String moId, String factoryId) {
        List<WipMtrRtcAssignEntity> rtcAssignList = new ArrayList<>();
        List<WipReqLotIssuedEntity> reqLotIssuedList = wipReqLotIssuedRepository.selectByKey(this.organizationId, moId, this.itemNo, this.wkpNo);
        if (ListUtil.notEmpty(reqLotIssuedList) && reqLotIssuedList.size() == 1) {
            // 仅有一个批次时自动生成
            WipReqLotIssuedEntity reqLotIssued = reqLotIssuedList.get(0);
            WipMtrRtcAssignEntity rtcAssign = WipMtrRtcAssignEntity.get();
            rtcAssign.setAssignId(UUIDUtils.get32UUID())
                    .setLineId(this.lineId)
                    .setHeaderId(this.headerId)
                    .setOrganizationId(this.organizationId)
                    .setFactoryId(factoryId)
                    .setInvpNo(this.invpNo)
                    .setMtrLotNo(reqLotIssued.getMtrLotNo())
                    .setAssignQty(this.reqQty)
                    .setIssuedQty(this.issuedQty)
                    .setAssignStatus(StatusEnum.NORMAL.getCode())
                    .setLotControlType(reqLotIssued.getLotType());
            rtcAssignList.add(rtcAssign);
        }
        this.setAssignList(rtcAssignList);
        return rtcAssignList;
    }

    public void createAssigns(List<WipMtrRtcAssignEntity> rtcAssignEntityList) {
        rtcAssignEntityList.forEach(assign -> EntityUtils.writeStdCrtInfoToEntity(assign, EntityUtils.getWipUserId()));
        wipMtrRtcAssignRepository.insertList(rtcAssignEntityList);
    }

    public void updateAssigns(List<WipMtrRtcAssignEntity> rtcAssignEntityList) {
        rtcAssignEntityList.forEach(assign -> EntityUtils.writeStdUpdInfoToEntity(assign, EntityUtils.getWipUserId()));
        for (WipMtrRtcAssignEntity updateAssign : rtcAssignEntityList) {
            wipMtrRtcAssignRepository.updateSelectiveById(updateAssign);
        }
    }

    public void deleteAssign() {
        List<WipMtrRtcAssignEntity> assignList = this.getAssignList();
        if (ListUtil.notEmpty(assignList)) {
            assignList.forEach(assign -> assign.setAssignStatus(StatusEnum.CLOSE.getCode()));
            wipMtrRtcAssignRepository.updateList(assignList);
        }
    }

    public void deleteAssigns(List<WipMtrRtcAssignEntity> rtcAssignEntityList) {
        for (WipMtrRtcAssignEntity deleteAssignEntity : rtcAssignEntityList) {
            deleteAssignEntity.setAssignStatus(StatusEnum.CLOSE.getCode());
            EntityUtils.writeStdUpdInfoToEntity(deleteAssignEntity, EntityUtils.getWipUserId());
        }
        wipMtrRtcAssignRepository.updateList(rtcAssignEntityList);
    }

    public void deleteLineAssigns(List<WipMtrRtcLineEntity> rtcLineList) {
        List<String> lineIdList = rtcLineList.stream().map(WipMtrRtcLineEntity::getLineId).collect(Collectors.toList());
        List<WipMtrRtcAssignEntity> closeAssignList = WipMtrRtcAssignEntity.get().getByLineIds(lineIdList);
        closeAssignList = closeAssignList.stream().filter(assign -> StatusEnum.NORMAL.getCode().equals(assign.getAssignStatus())).collect(Collectors.toList());
        if (ListUtil.empty(closeAssignList)) {
            return;
        }
        for (WipMtrRtcAssignEntity assign : closeAssignList) {
            assign.setAssignStatus(StatusEnum.CLOSE.getCode());
            EntityUtils.writeStdUpdInfoToEntity(assign, EntityUtils.getWipUserId());
        }
        wipMtrRtcAssignRepository.updateList(closeAssignList);
    }

    public String getReqKey(String moId) {
        return String.join("_", this.organizationId, moId, this.itemId, this.wkpNo);
    }

    private void wiredAfterSelect(WipMtrRtcLineEntity rtcLineEntity) {
        rtcLineEntity.setWipMtrRtcLineRepository(this.wipMtrRtcLineRepository);
        rtcLineEntity.setWipMtrRtcAssignRepository(this.wipMtrRtcAssignRepository);
        rtcLineEntity.setWipReqLotIssuedRepository(this.wipReqLotIssuedRepository);
    }

    public static WipMtrRtcLineEntity get() {
        return DomainFactory.get(WipMtrRtcLineEntity.class);
    }

}
