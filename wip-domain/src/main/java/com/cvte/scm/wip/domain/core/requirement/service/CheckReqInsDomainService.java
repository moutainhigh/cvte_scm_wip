package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
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

    private ReqInsRepository insRepository;
    private QueryReqLineService queryReqLineService;
    private WipReqLineRepository wipReqLineRepository;

    public CheckReqInsDomainService(ReqInsRepository insRepository, QueryReqLineService queryReqLineService, WipReqLineRepository wipReqLineRepository) {
        this.insRepository = insRepository;
        this.queryReqLineService = queryReqLineService;
        this.wipReqLineRepository = wipReqLineRepository;
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
                    List<WipReqLineEntity> reqLineList = queryReqLineService.getValidLine(keyQueryVO);
                    if (!InsOperationTypeEnum.INCREASE.getCode().equals(detailEntity.getOperationType())) {
                        this.validateTargetLine(keyQueryVO, reqLineList);
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

    private void validateTargetLine(WipReqLineKeyQueryVO keyQueryVO, List<WipReqLineEntity> reqLineList) {
        if (ListUtil.empty(reqLineList)) {
            // 当主料查询不到对应要 删除/减少/替换前 的投料行时, 尝试用替代料查询
            List<WipReqLineEntity> mtrsItemLineList = queryReqLineService.getReqLinesByMtrsItem(keyQueryVO);
            reqLineList.addAll(mtrsItemLineList);
            if (ListUtil.empty(reqLineList)) {
                // 如果都查询不到, 则报错
                throw new ServerException(ReqInsErrEnum.TARGET_LINE_INVALID.getCode(), ReqInsErrEnum.TARGET_LINE_INVALID.getDesc());
            }
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
     * @author xueyuting
     * @since 2020/5/28 11:03 上午
     */
    public void checkLineStatus(ReqInsEntity insEntity) {
        boolean validateFailed = false;
        // 一个更改单的多个指令明细可能针对同一个物料的不同小批次和位号, 校验领料时只考虑到物料维度, 因此只查询到物料维度
        Map<String, List<ReqInsDetailEntity>> detailGroupedByItem = insEntity.getDetailList().stream().collect(Collectors.groupingBy(detail ->
                detail.getWkpNo() + "_" + detail.getItemIdOld() + "_" + detail.getOperationType()
        ));
        for (List<ReqInsDetailEntity> detailGroupedByItemList : detailGroupedByItem.values()) {
            ReqInsDetailEntity randomDetail = detailGroupedByItemList.get(0);
            if (InsOperationTypeEnum.ADD.getCode().equals(randomDetail.getOperationType())
                    || InsOperationTypeEnum.INCREASE.getCode().equals(randomDetail.getOperationType())) {
                // 新增或增加类型无需校验
                continue;
            }
            List<WipReqLineEntity> reqLineInItemDim = wipReqLineRepository.selectByItemDim(randomDetail.getOrganizationId(), randomDetail.getAimHeaderId(), randomDetail.getWkpNo(), randomDetail.getItemIdOld());
            // 计算该物料更改指令的更改数量之和
            int changeQty = detailGroupedByItemList.stream().map(detail -> Optional.ofNullable(detail.getItemQty()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add).intValue();
            try {
                validateTargetLineIssued(reqLineInItemDim, changeQty);
            } catch (ServerException se) {
                validateFailed = true;
                detailGroupedByItemList.forEach(detail -> detail.setExecuteResult(se.getMessage()));
            }
        }

        if (validateFailed) {
            throw new ServerException(ReqInsErrEnum.TARGET_LINE_ISSUED.getCode(), ReqInsErrEnum.TARGET_LINE_ISSUED.getDesc());
        }
    }

    private void validateTargetLineIssued(List<WipReqLineEntity> reqLineInItemDim, int changeQty) {
        if (changeQty <= 0) {
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc() + ",更改数量为0");
        }
        // 若有一个投料行已领料, 则视为已领料
        boolean issued = reqLineInItemDim.stream().map(WipReqLineEntity::getLineStatus).anyMatch(status -> BillStatusEnum.ISSUED.getCode().equals(status));
        if (issued) {
            // 若状态为已领料, 则计算 可变更数量 = 需求数量 - 领料数量
            int reqQty = reqLineInItemDim.stream().mapToInt(line -> Optional.ofNullable(line.getReqQty()).orElse(0)).sum();
            int issuedQty = reqLineInItemDim.stream().mapToInt(line -> Optional.ofNullable(line.getIssuedQty()).orElse(0)).sum();
            boolean enoughChangeQty = (reqQty - issuedQty) >= changeQty;
            if (!enoughChangeQty) {
                // 未领料数量 < 变更数量 时无法变更数量
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
                            || ProcessingStatusEnum.CLOSE.getCode().equals(existsIns.getStatus()));
        }
        return false;
    }

}
