package com.cvte.scm.wip.domain.core.rtc.entity;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.audit.AuditField;
import com.cvte.scm.wip.common.audit.AuditEntity;
import com.cvte.scm.wip.common.audit.AuditId;
import com.cvte.scm.wip.common.audit.AuditParentId;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.common.serial.SerialNoGenerationService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcHeaderRepository;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderStatusEnum;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderStatusEnum.*;

/**
 * 领退料单
 *
 * @author xueyuting
 * @since 2020-09-08
 */
@Data
@Slf4j
@Component
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AuditEntity(modelName = "rtc", entityName = "header")
public class WipMtrRtcHeaderEntity extends BaseModel implements Entity<String> {

    private static String SERIAL_CODE = "SCM_WIP_MTR_RTC_BILL_NO";

    private WipMtrRtcHeaderRepository wipMtrRtcHeaderRepository;
    private WipMtrRtcLineRepository wipMtrRtcLineRepository;
    private SerialNoGenerationService serialNoGenerationService;

    public WipMtrRtcHeaderEntity() {}

    @Autowired
    public WipMtrRtcHeaderEntity(WipMtrRtcHeaderRepository wipMtrRtcHeaderRepository, WipMtrRtcLineRepository wipMtrRtcLineRepository, SerialNoGenerationService serialNoGenerationService) {
        this.wipMtrRtcHeaderRepository = wipMtrRtcHeaderRepository;
        this.wipMtrRtcLineRepository = wipMtrRtcLineRepository;
        this.serialNoGenerationService = serialNoGenerationService;
    }

    @Override
    public String getUniqueId() {
        return headerId;
    }

    @AuditId
    @AuditParentId
    private String headerId;

    private String organizationId;

    private String billNo;

    @AuditField(fieldName = "类型")
    private String billType;

    private String moId;

    @AuditField(fieldName = "工序")
    private String wkpNo;

    private String deptNo;

    private String factoryId;

    @AuditField(fieldName = "备注")
    private String remark;

    @AuditField(fieldName = "领料套数")
    private BigDecimal billQty;

    @AuditField(fieldName = "子库")
    private String invpNo;

    private String billStatus;

    private String sourceBillNo;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

    private String reviewer;

    private Date reviewTime;

    private List<WipMtrRtcLineEntity> lineList;

    public WipMtrRtcHeaderEntity getById(String headerId) {
        if (StringUtils.isBlank(headerId)) {
            throw new ParamsIncorrectException("单据ID不可为空");
        }
        WipMtrRtcHeaderEntity selectEntity = wipMtrRtcHeaderRepository.selectById(headerId);
        this.wiredAfterSelect(selectEntity);
        return selectEntity;
    }

    public List<WipMtrRtcLineEntity> getLineList() {
        if (ListUtil.notEmpty(this.lineList)) {
            return this.lineList;
        }
        if (StringUtils.isBlank(this.headerId)) {
            return new ArrayList<>();
        }
        this.lineList = WipMtrRtcLineEntity.get().getByHeaderId(this.headerId);
        return lineList;
    }

    public void create(WipMtrRtcHeaderBuildVO headerBuildVO) {
        this.setHeaderId(UUIDUtils.get32UUID())
                .setOrganizationId(headerBuildVO.getOrganizationId())
                .setBillType(headerBuildVO.getBillType())
                .setMoId(headerBuildVO.getMoId())
                .setWkpNo(headerBuildVO.getWkpNo())
                .setBillQty(headerBuildVO.getBillQty())
                .setDeptNo(headerBuildVO.getDeptNo())
                .setFactoryId(headerBuildVO.getFactoryId())
                .setRemark(headerBuildVO.getRemark())
                .setInvpNo(headerBuildVO.getInvpNo())
                .setSourceBillNo(headerBuildVO.getSourceBillNo())
                .setBillStatus(DRAFT.getCode());
    }

    public void update(WipMtrRtcHeaderBuildVO headerBuildVO) {
        this.setMoId(headerBuildVO.getMoId())
                .setWkpNo(headerBuildVO.getWkpNo())
                .setBillType(headerBuildVO.getBillType())
                .setBillQty(headerBuildVO.getBillQty())
                .setInvpNo(headerBuildVO.getInvpNo())
                .setDeptNo(headerBuildVO.getDeptNo())
                .setRemark(headerBuildVO.getRemark())
                .setSourceBillNo(headerBuildVO.getSourceBillNo());
    }

    public void save(boolean isCreate) {
        if (isCreate) {
            // 流水单据号
            this.setBillNo(serialNoGenerationService.getNextSerialNumberByCode(SERIAL_CODE));
            EntityUtils.writeStdCrtInfoToEntity(this, EntityUtils.getWipUserId());
            wipMtrRtcHeaderRepository.insert(this);
        } else {
            EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
            wipMtrRtcHeaderRepository.updateSelectiveById(this);
        }
    }

    public void update() {
        EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
        wipMtrRtcHeaderRepository.updateSelectiveById(this);
    }

    public void submit() {
        this.setBillStatus(REVIEW.getCode());
        EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
        wipMtrRtcHeaderRepository.updateSelectiveById(this);
    }

    public void review(String approved) {
        String approvedBillStatus;
        if (BooleanEnum.YES.getCode().equals(approved)) {
            approvedBillStatus = EFFECTIVE.getCode();
        } else if (BooleanEnum.NO.getCode().equals(approved)) {
            approvedBillStatus = WITHDRAW.getCode();
        } else {
            throw new ParamsIncorrectException("非法的审核指令");
        }
        this.setBillStatus(approvedBillStatus)
                .setReviewer(EntityUtils.getWipUserId())
                .setReviewTime(new Date());
        EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
        wipMtrRtcHeaderRepository.updateSelectiveById(this);
    }

    public boolean close() {
        this.checkClosable();
        this.setBillStatus(CLOSED.getCode());
        EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
        List<WipMtrRtcLineEntity> unCompleteRtcLineList = getLineList().stream().filter(line -> WipMtrRtcLineStatusEnum.getUnPostStatus().contains(line.getLineStatus())).collect(Collectors.toList());
        // 关闭未过账的行
        WipMtrRtcLineEntity.get().batchClose(unCompleteRtcLineList);
        // 关闭单据
        wipMtrRtcHeaderRepository.updateSelectiveById(this);

        // 是否已有过账的行
        List<WipMtrRtcLineEntity> postLineList = getLineList().stream().filter(line -> WipMtrRtcLineStatusEnum.POSTED.getCode().equals(line.getLineStatus())).collect(Collectors.toList());
        return ListUtil.notEmpty(postLineList);
    }

    public void cancel() {
        this.checkCancelable();
        this.setBillStatus(CANCELED.getCode());
        EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
        List<WipMtrRtcLineEntity> unPostRtcLineList = getLineList().stream().filter(line -> WipMtrRtcLineStatusEnum.getUnPostStatus().contains(line.getLineStatus())).collect(Collectors.toList());
        WipMtrRtcLineEntity.get().batchCancel(unPostRtcLineList);
        wipMtrRtcHeaderRepository.updateSelectiveById(this);
    }

    public void post() {
        List<WipMtrRtcLineEntity> rtcLineList = WipMtrRtcLineEntity.get().getByHeaderId(this.headerId);;
        List<WipMtrRtcLineEntity> unPostLineList = rtcLineList.stream().filter(line -> WipMtrRtcLineStatusEnum.getUnPostStatus().contains(line.getLineStatus())).collect(Collectors.toList());
        EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
        if (ListUtil.empty(unPostLineList)) {
            // 所有行均过账, 更新状态为已完成
            this.setBillStatus(COMPLETED.getCode());
            wipMtrRtcHeaderRepository.updateSelectiveById(this);
        } else {
            // 还有未过账的行, 更新状态为部分过账; 若已是部分过账, 无需更新
            if (!POSTING.getCode().equals(this.getBillStatus())) {
                this.setBillStatus(POSTING.getCode());
                wipMtrRtcHeaderRepository.updateSelectiveById(this);
            }
        }
    }

    /**
     * 生成单据行
     * @since 2020/9/8 5:52 下午
     * @author xueyuting
     */
    public void generateLines(List<WipReqItemVO> reqItemVOList) {
        List<WipMtrRtcLineEntity> mtrRtcLineEntityList = new ArrayList<>();
        for (WipReqItemVO reqItemVO : reqItemVOList) {
            WipMtrRtcLineEntity rtcLineEntity = WipMtrRtcLineEntity.get();
            rtcLineEntity.setLineId(UUIDUtils.get32UUID())
                    .setHeaderId(this.headerId)
                    .setOrganizationId(this.organizationId)
                    .setItemId(reqItemVO.getItemId())
                    .setItemNo(reqItemVO.getItemNo())
                    .setWkpNo(reqItemVO.getWkpNo())
                    .setInvpNo(this.invpNo)
                    .setReqQty(this.billQty)
                    .setIssuedQty(this.billQty)
                    .setLineStatus(WipMtrRtcLineStatusEnum.ASSIGNED.getCode());
            mtrRtcLineEntityList.add(rtcLineEntity);
        }
        this.lineList = mtrRtcLineEntityList;
    }

    public void adjustLines(WipMtrRtcHeaderBuildVO rtcHeaderBuildVO, List<WipReqItemVO> reqItemVOList) {
        if (Objects.isNull(billQty)) {
            return;
        }
        Map<String, WipReqItemVO> reqItemVOMap = reqItemVOList.stream().collect(Collectors.toMap(WipReqItemVO::getKey, Function.identity()));
        Iterator<WipMtrRtcLineEntity> iterator = this.getLineList().iterator();
        while (iterator.hasNext()) {
            WipMtrRtcLineEntity rtcLineEntity = iterator.next();
            // 获取物料投料信息
            String reqItemMapKey = rtcLineEntity.getReqKey(rtcHeaderBuildVO.getMoId());
            WipReqItemVO reqItemVO = reqItemVOMap.get(reqItemMapKey);
            if (Objects.isNull(reqItemVO)) {
                log.warn("{}:领料行没有对应的投料需求", reqItemMapKey);
                iterator.remove();
                continue;
            }

            // 计算数量
            BigDecimal rtcLineReqQty = rtcLineEntity.calculateQty(rtcHeaderBuildVO, reqItemVO).setScale(0, RoundingMode.CEILING);
            rtcLineEntity.setReqQty(rtcLineReqQty)
                    .setIssuedQty(rtcLineReqQty);
            if (rtcLineReqQty.compareTo(BigDecimal.ZERO) <= 0) {
                rtcLineEntity.setLineStatus(WipMtrRtcLineStatusEnum.CANCELED.getCode());
            }
        }
    }

    public void saveLines(boolean isCreate) {
        if (isCreate) {
            for (WipMtrRtcLineEntity rtcLineEntity : this.lineList) {
                EntityUtils.writeStdCrtInfoToEntity(rtcLineEntity, EntityUtils.getWipUserId());
            }
            wipMtrRtcLineRepository.insertList(this.lineList);
        } else {
            for (WipMtrRtcLineEntity rtcLineEntity : this.lineList) {
                EntityUtils.writeStdUpdInfoToEntity(rtcLineEntity, EntityUtils.getWipUserId());
            }
            wipMtrRtcLineRepository.updateList(this.lineList);
        }
    }

    public void invalidLines() {
        List<WipMtrRtcLineEntity> lineEntityList = getLineList();
        for (WipMtrRtcLineEntity rtcLineEntity : lineEntityList) {
            rtcLineEntity.setLineStatus(WipMtrRtcLineStatusEnum.CANCELED.getCode());
            EntityUtils.writeStdUpdInfoToEntity(rtcLineEntity, EntityUtils.getWipUserId());
        }
        wipMtrRtcLineRepository.updateList(lineEntityList);
    }

    public void checkCancelable() {
        if (!WipMtrRtcHeaderStatusEnum.getCancelableStatus().stream().map(WipMtrRtcHeaderStatusEnum::getCode).collect(Collectors.toSet()).contains(this.getBillStatus())) {
            throw new ParamsIncorrectException(String.format("仅%s状态的单据可取消", String.join("/", WipMtrRtcHeaderStatusEnum.getCancelableStatus().stream().map(WipMtrRtcHeaderStatusEnum::getDesc).collect(Collectors.toSet()))));
        }
    }

    public void checkClosable() {
        if (!WipMtrRtcHeaderStatusEnum.POSTING.getCode().equals(this.getBillStatus())) {
            throw new ParamsIncorrectException("仅部分过账的单据可以关闭");
        }
    }

    private void wiredAfterSelect(WipMtrRtcHeaderEntity selectEntity) {
        selectEntity.setWipMtrRtcHeaderRepository(this.wipMtrRtcHeaderRepository)
                .setWipMtrRtcLineRepository(this.wipMtrRtcLineRepository)
                .setSerialNoGenerationService(this.serialNoGenerationService);
    }

    public static WipMtrRtcHeaderEntity get() {
        return DomainFactory.get(WipMtrRtcHeaderEntity.class);
    }

}
