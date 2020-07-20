package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.valueobject.enums.ChangeBillRecycleEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/5/25 17:02
 */
@Service
public class CheckReqInsDomainService implements DomainService {

    private WipReqLineRepository lineRepository;
    private ReqInsRepository insRepository;

    public CheckReqInsDomainService(WipReqLineRepository lineRepository, ReqInsRepository insRepository) {
        this.lineRepository = lineRepository;
        this.insRepository = insRepository;
    }

    /**
     * 校验指令指令是否已执行
     *
     * @param
     * @author xueyuting
     * @since 2020/6/14 11:05 上午
     */
    public void checkInsProcessed(ReqInsEntity insEntity) {
        if (!(ProcessingStatusEnum.PENDING.getCode().equals(insEntity.getStatus()) || ProcessingStatusEnum.EXCEPTION.getCode().equals(insEntity.getStatus()))) {
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc());
        }
    }

    /**
     * 指令的目标投料行需有效
     *
     * @author xueyuting
     * @since 2020/6/1 2:21 下午
     */
    public Map<String, List<WipReqLineEntity>> validAndGetLine(ReqInsEntity insEntity) {
        Map<String, List<WipReqLineEntity>> reqLineMap = new HashMap<>();
        boolean validateFailed = false;
        if (ListUtil.empty(insEntity.getDetailList())) {
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc());
        }
        for (ReqInsDetailEntity detailEntity : insEntity.getDetailList()) {
            try {
                WipReqLineKeyQueryVO keyQueryVO = WipReqLineKeyQueryVO.build(detailEntity);
                if (!InsOperationTypeEnum.ADD.getCode().equals(detailEntity.getOperationType())) {
                    // 非新增或增加(因为增加对象不存在时新增), 获取指令的目标投料行
                    List<String> statusList = new ArrayList<>();
                    statusList.add(BillStatusEnum.DRAFT.getCode());
                    statusList.add(BillStatusEnum.CONFIRMED.getCode());
                    statusList.add(BillStatusEnum.PREPARED.getCode());
                    statusList.add(BillStatusEnum.ISSUED.getCode());
                    List<WipReqLineEntity> reqLineList = lineRepository.selectValidByKey(keyQueryVO, statusList);
                    if (!InsOperationTypeEnum.INCREASE.getCode().equals(detailEntity.getOperationType())) {
                        this.validateTargetLine(reqLineList);
                    }
                    reqLineMap.put(detailEntity.getInsDetailId(), reqLineList);
                } else {
                    this.validateIncreaseQty(detailEntity);
                }
            } catch (ServerException se) {
                validateFailed = true;
                detailEntity.setExecuteResult(se.getMessage());
            }
        }
        if (validateFailed) {
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), "投料行不存在或用量为空");
        }
        return reqLineMap;
    }

    private void validateTargetLine(List<WipReqLineEntity> reqLineList) {
        if (ListUtil.empty(reqLineList)) {
            throw new ServerException(ReqInsErrEnum.TARGET_LINE_INVALID.getCode(), ReqInsErrEnum.TARGET_LINE_INVALID.getDesc());
        }
    }

    private void validateIncreaseQty(ReqInsDetailEntity detailEntity) {
        if (Objects.isNull(detailEntity.getItemUnitQty())) {
            throw new ServerException(ReqInsErrEnum.ADD_VALID_QTY.getCode(), ReqInsErrEnum.ADD_VALID_QTY.getDesc());
        }
    }

    /**
     * 已领料/已关闭不允许变更
     *
     * @param
     * @author xueyuting
     * @since 2020/5/28 11:03 上午
     */
    public void checkLineStatus(ReqInsEntity insEntity, Map<String, List<WipReqLineEntity>> reqLineMap) {
        if (Objects.isNull(reqLineMap) || reqLineMap.isEmpty()) {
            reqLineMap = validAndGetLine(insEntity);
        }
        boolean validateFailed = false;
        for (ReqInsDetailEntity detailEntity : insEntity.getDetailList()) {
            if (InsOperationTypeEnum.ADD.getCode().equals(detailEntity.getOperationType())
                    || InsOperationTypeEnum.INCREASE.getCode().equals(detailEntity.getOperationType())) {
                // 新增或增加类型无需校验
                continue;
            }
            List<WipReqLineEntity> reqLineList = reqLineMap.get(detailEntity.getInsDetailId());
            try {
                if (ChangeBillRecycleEnum.RECYCLE.getCode().equals(detailEntity.getIssueFlag())) {
                    // 仅当 回收发料 时需要校验是否已领料
                    this.validateTargetLineIssued(reqLineList, detailEntity.getItemQty());
                }
            } catch (ServerException se) {
                validateFailed = true;
                detailEntity.setExecuteResult(se.getMessage());
            }
        }
        if (validateFailed) {
            throw new ServerException(ReqInsErrEnum.TARGET_LINE_ISSUED.getCode(), ReqInsErrEnum.TARGET_LINE_ISSUED.getDesc());
        }
    }

    private void validateTargetLineIssued(List<WipReqLineEntity> reqLineList, BigDecimal changeQty) {
        for (WipReqLineEntity reqLine : reqLineList) {
            // 状态为领料 或者 领料数量大于0, 认为已领料
            boolean isIssued = BillStatusEnum.ISSUED.getCode().equals(reqLine.getLineStatus()) || (Objects.nonNull(reqLine.getIssuedQty()) && reqLine.getIssuedQty() > 0);
            if (isIssued) {
                int canChangeQty = Optional.ofNullable(reqLine.getReqQty()).orElse(0) - reqLine.getIssuedQty();
                boolean enoughChangeQty = Objects.nonNull(changeQty) && canChangeQty >= changeQty.intValue();
                if (enoughChangeQty) {
                    // 已领料, 但是更改数量 < (需求数量-领料数量)(即可更改数量) 时 允许变更
                    continue;
                }
                throw new ServerException(ReqInsErrEnum.TARGET_LINE_ISSUED.getCode(), ReqInsErrEnum.TARGET_LINE_ISSUED.getDesc());
            }
        }
    }

    /**
     * 部分混料不允许变更, 即替换数量不等于原数量
     *
     * @author xueyuting
     * @since 2020/5/28 11:04 上午
     */
    public void checkPartMix(ReqInsEntity insEntity, Map<String, List<WipReqLineEntity>> reqLineMap) {
        if (Objects.isNull(reqLineMap) || reqLineMap.isEmpty()) {
            reqLineMap = validAndGetLine(insEntity);
        }
        for (ReqInsDetailEntity detailEntity : insEntity.getDetailList()) {
            if (InsOperationTypeEnum.REPLACE.getCode().equals(detailEntity.getOperationType()) && Objects.nonNull(detailEntity.getItemQty())) {
                List<WipReqLineEntity> reqLineList = reqLineMap.get(detailEntity.getInsDetailId());
                long reqQty = reqLineList.stream().filter(line -> Objects.nonNull(line.getReqQty())).mapToLong(WipReqLineEntity::getReqQty).sum();
                long replaceQty = detailEntity.getItemQty().longValue();
                if (reqQty != replaceQty) {
                    throw new ServerException(ReqInsErrEnum.PART_MIX.getCode(), ReqInsErrEnum.PART_MIX.getDesc());
                }
            }
        }
    }

    /**
     * 存在未执行的前置指令不允许变更
     *
     * @author xueyuting
     * @since 2020/6/1 2:28 下午
     */
    public void checkPreInsExists(ReqInsEntity insEntity) {
        List<String> statusList = new ArrayList<>();
        statusList.add(ProcessingStatusEnum.PENDING.getCode());
        statusList.add(ProcessingStatusEnum.EXCEPTION.getCode());
        List<ReqInsEntity> sameAimInsList = insEntity.getByAimHeaderId(insEntity.getAimHeaderId(), statusList);
        sameAimInsList.removeIf(ins -> ins.getInsHeaderId().equals(insEntity.getInsHeaderId()));
        List<ReqInsEntity> preInsList = new ArrayList<>();
        if (ListUtil.notEmpty(sameAimInsList)) {
            for (ReqInsEntity sameAimIns : sameAimInsList) {
                if (sameAimIns.getEnableDate().before(insEntity.getEnableDate())) {
                    preInsList.add(sameAimIns);
                }
            }
            if (ListUtil.empty(preInsList)) {
                return;
            }

            List<String> cnBillIdList = preInsList.stream().map(ReqInsEntity::getSourceChangeBillId).collect(Collectors.toList());
            List<ChangeBillEntity> changeBillList = ChangeBillEntity.get().getById(cnBillIdList);
            List<String> billNoList = changeBillList.stream().map(ChangeBillEntity::getBillNo).collect(Collectors.toList());
            throw new ServerException(ReqInsErrEnum.EXISTS_PRE_INS.getCode(), ReqInsErrEnum.EXISTS_PRE_INS.getDesc() + "," + String.join(",", billNoList));
        }
    }

    public void checkInsPrepared(List<String> insHeaderIdList) {
        List<String> preparedBillNoList = insRepository.getPreparedById(insHeaderIdList);
        if (ListUtil.notEmpty(preparedBillNoList)) {
            throw new ParamsIncorrectException("更改单:" + String.join(",", preparedBillNoList) + "已备料");
        }
    }

    /**
     * 校验投料指令是否已执行
     *
     * @author xueyuting
     * @since 2020/6/9 8:23 下午
     */
    public Boolean checkInsProcessed(ReqInsBuildVO vo) {
        if (StringUtils.isNotBlank(vo.getInsHeaderId())) {
            ReqInsEntity existsIns = ReqInsEntity.get().getByKey(vo.getInsHeaderId());
            return Objects.nonNull(existsIns) &&
                    (ProcessingStatusEnum.SOLVED.getCode().equals(existsIns.getStatus())
                            || StatusEnum.CLOSE.getCode().equals(existsIns.getStatus()));
        }
        return false;
    }

}
