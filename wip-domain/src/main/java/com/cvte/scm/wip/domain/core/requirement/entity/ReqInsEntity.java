package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainEventPublisher;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.event.ReqInsProcessNotifyEvent;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInsEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/5/21 16:22
 */
@Slf4j
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqInsEntity implements Entity<String> {

    @Resource
    private ReqInsEntityFactory headerEntityFactory;

    @Resource
    private DomainEventPublisher domainEventPublisher;

    private ReqInsRepository headerRepository;

    public ReqInsEntity(ReqInsRepository headerRepository) {
        this.headerRepository = headerRepository;
    }

    @Override
    public String getUniqueId() {
        return insHeaderId;
    }

    private String insHeaderId;

    private String sourceChangeBillId;

    private String status;

    private String aimHeaderId;

    private String aimReqLotNo;

    private Date enableDate;

    private Date disableDate;

    private String confirmedBy;

    private String invalidBy;

    private String invalidReason;

    private String executeResult;

    private List<ReqInsDetailEntity> detailList = Collections.emptyList();

    public ReqInsEntity getByKey(String insKey) {
        return headerRepository.selectByKey(insKey);
    }

    public List<ReqInsEntity> getByAimHeaderId(String aimHeaderId, List<String> statusList) {
        return headerRepository.selectByAimHeaderId(aimHeaderId, statusList);
    }

    public List<ReqInsDetailEntity> getDetailById() {
        if (ListUtil.notEmpty(this.detailList)) {
            return this.detailList;
        }
        this.detailList = ReqInsDetailEntity.get().getByInstructionId(this.insHeaderId);
        // 冗余目标投料单头ID和批次
        this.detailList.forEach(detail -> detail.setAimHeaderId(this.getAimHeaderId())
                .setAimReqLotNo(this.getAimReqLotNo()));
        return this.detailList;
    }

    public void updateInstruction() {
        headerRepository.update(this);
    }

    public ReqInsEntity createInstruction(ReqInsBuildVO vo) {
        String newInstructionId = UUIDUtils.get32UUID();
        vo.setInsHeaderId(newInstructionId);
        vo.getDetailList().forEach(detail -> detail.setInsHeaderId(newInstructionId));
        ReqInsEntity instructionEntity = headerEntityFactory.perfect(vo);
        headerRepository.insert(instructionEntity);
        return instructionEntity;
    }

    public ReqInsEntity updateInstruction(ReqInsBuildVO vo) {
        ReqInsEntity instructionEntity = headerEntityFactory.perfect(vo);
        headerRepository.update(instructionEntity);
        return instructionEntity;
    }

    public void invalidInstruction() {
        this.setStatus(ProcessingStatusEnum.CLOSE.getCode());
        if (StringUtils.isBlank(this.getInvalidBy())) {
            this.setInvalidBy(EntityUtils.getWipUserId());
        }
        headerRepository.update(this);
    }

    public ReqInsEntity createCompleteReqIns(ReqInsBuildVO vo) {
        ReqInsEntity billEntity = this.createInstruction(vo);
        List<ReqInsDetailEntity> detailEntityList = ReqInsDetailEntity.get().batchCreateDetail(vo.getDetailList());
        billEntity.setDetailList(detailEntityList);
        return billEntity;
    }

    public ReqInsEntity updateCompleteReqIns(ReqInsBuildVO vo) {
        ReqInsEntity billEntity = this.updateInstruction(vo);
        List<ReqInsDetailEntity> detailEntityList = ReqInsDetailEntity.get().batchSaveInsDetail(vo, true);
        billEntity.setDetailList(detailEntityList);
        return billEntity;
    }

    public ReqInsEntity deleteCompleteReqIns(ReqInsBuildVO vo) {
        vo.setInvalidBy(EntityUtils.getWipUserId());
        ReqInsEntity billEntity = headerEntityFactory.perfect(vo);
        billEntity.invalidInstruction();
        List<ReqInsDetailEntity> detailEntityList = billEntity.getDetailById();
        detailEntityList.forEach(detail -> detail.setInvalidReason(vo.getInvalidReason()).setInvalidBy(vo.getInvalidBy()));
        ReqInsDetailEntity.get().batchDeleteDetail(detailEntityList);
        return billEntity;
    }

    /**
     * 生成投料单指令集及其所有指令
     *
     * @author xueyuting
     * @since 2020/5/23 10:40 上午
     */
    public ReqInsEntity completeInstruction(ReqInsBuildVO vo) {
        if (ChangedTypeEnum.DELETE.getCode().equals(vo.getChangeType())) {
            return this.deleteCompleteReqIns(vo);
        } else if (ChangedTypeEnum.UPDATE.getCode().equals(vo.getChangeType())) {
            return this.updateCompleteReqIns(vo);
        } else {
            return this.createCompleteReqIns(vo);
        }
    }

    public void processSuccess() {
        this.setStatus(ProcessingStatusEnum.SOLVED.getCode());
        this.setConfirmedBy(EntityUtils.getWipUserId());
        this.setExecuteResult("成功");
        headerRepository.update(this);
        ReqInsDetailEntity.get().batchProcessSuccess(this.getDetailList());
    }

    public void processFailed(String errMsg) {
        this.setStatus(ProcessingStatusEnum.EXCEPTION.getCode());
        this.setExecuteResult(errMsg);
        headerRepository.update(this);
        ReqInsDetailEntity.get().batchProcessFailed(this.getDetailList());
        this.notifyEntity();
    }

    public List<WipReqLineEntity> parse(Map<String, List<WipReqLineEntity>> reqLineMap) {
        List<WipReqLineEntity> reqLineEntityList = new ArrayList<>();
        boolean parseFailed = false;
        for (ReqInsDetailEntity detailEntity : this.getDetailList()) {
            try {
                List<WipReqLineEntity> detailLineList = detailEntity.parseDetail(reqLineMap);
                detailLineList.forEach(line -> line.setInsDetailId(detailEntity.getInsDetailId()));
                reqLineEntityList.addAll(detailLineList);
            } catch (RuntimeException se) {
                parseFailed = true;
                detailEntity.setExecuteResult(se.getMessage());
            }
        }
        if (parseFailed) {
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), "指令解析失败");
        }
        return reqLineEntityList;
    }

    public String groupDetailExecuteResult() {
        // 以 执行结果为key 将ID用","聚合
        Map<String, String> resultMap = this.detailList.stream()
                .filter(detail -> StringUtils.isNotBlank(detail.getExecuteResult()))
                .collect(Collectors.groupingBy(ReqInsDetailEntity::getExecuteResult, Collectors.mapping(ReqInsDetailEntity::getInsDetailId, Collectors.joining(","))));
        if (resultMap == null || resultMap.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> resultEntry : resultMap.entrySet()) {
            sb.append(resultEntry.getKey()).append(", id:").append(resultEntry.getValue()).append(";");
        }
        return sb.toString();
    }

    public void revertStatus() {
        this.setStatus(ProcessingStatusEnum.PENDING.getCode())
                .setConfirmedBy("").setExecuteResult("")
                .setInvalidBy("").setInvalidReason("");
        headerRepository.update(this);
        this.getDetailList().forEach(ReqInsDetailEntity::revertStatus);
    }

    public void revertIns() {
        this.getDetailList().forEach(ReqInsDetailEntity::revertItem);
    }

    public void notifyEntity() {
        domainEventPublisher.publish(new ReqInsProcessNotifyEvent(this), false);
    }

    public static ReqInsEntity get() {
        return DomainFactory.get(ReqInsEntity.class);
    }

}
