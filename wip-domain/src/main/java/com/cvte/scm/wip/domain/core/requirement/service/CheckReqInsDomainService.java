package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/25 17:02
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class CheckReqInsDomainService implements DomainService {

    private WipReqLineRepository lineRepository;

    public CheckReqInsDomainService(WipReqLineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    /**
     * 指令的目标投料行需有效
     * @since 2020/6/1 2:21 下午
     * @author xueyuting
     */
    public Map<String, List<WipReqLineEntity>> validAndGetLine(ReqInsEntity insEntity) {
        if (!(ProcessingStatusEnum.PENDING.getCode().equals(insEntity.getStatus()) || ProcessingStatusEnum.EXCEPTION.getCode().equals(insEntity.getStatus()))) {
            // 只有未执行和执行异常的指令可以执行
            List<String> sourceCnBillNoList = new ArrayList<>();
            sourceCnBillNoList.add(insEntity.getSourceChangeBillId());
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc() + "只有未执行或异常状态的指令可以执行,更改单:" + ChangeBillEntity.get().getById(sourceCnBillNoList).get(0).getBillNo());
        }
        Map<String, List<WipReqLineEntity>> reqLineMap = new HashMap<>();
        for (ReqInsDetailEntity detailEntity : insEntity.getDetailList()) {
            WipReqLineKeyQueryVO keyQueryVO = WipReqLineKeyQueryVO.build(detailEntity);
            if (!InsOperationTypeEnum.ADD.getCode().equals(detailEntity.getOperationType())) {
                // 非新增, 获取指令的目标投料行
                List<WipReqLineEntity> reqLineList = lineRepository.selectValidByKey(keyQueryVO);
                if (ListUtil.empty(reqLineList)) {
                    throw new ServerException(ReqInsErrEnum.TARGET_LINE_INVALID.getCode(), ReqInsErrEnum.TARGET_LINE_INVALID.getDesc() + detailEntity.toString());
                }
                reqLineMap.put(detailEntity.getInsDetailId(), reqLineList);
            } else {
                if (Objects.isNull(detailEntity.getItemUnitQty())) {
                    throw new ServerException(ReqInsErrEnum.ADD_VALID_QTY.getCode(), ReqInsErrEnum.ADD_VALID_QTY.getDesc() + detailEntity.toString());
                }
            }
        }
        return reqLineMap;
    }

    /**
     * 已领料/已关闭不允许变更
     * @since 2020/5/28 11:03 上午
     * @author xueyuting
     * @param
     */
    public void checkLineStatus(ReqInsEntity insEntity, Map<String, List<WipReqLineEntity>> reqLineMap) {
        if (Objects.isNull(reqLineMap) || reqLineMap.isEmpty()) {
            reqLineMap = validAndGetLine(insEntity);
        }
        for (ReqInsDetailEntity detailEntity : insEntity.getDetailList()) {
            if (InsOperationTypeEnum.ADD.getCode().equals(detailEntity.getOperationType())) {
                // 新增类型无需校验
                continue;
            }
            List<WipReqLineEntity> reqLineList = reqLineMap.get(detailEntity.getInsDetailId());
            for (WipReqLineEntity reqLine : reqLineList) {
                if (BillStatusEnum.ISSUED.getCode().equals(reqLine.getLineStatus())) {
                    throw new ServerException(ReqInsErrEnum.TARGET_LINE_ISSUED.getCode(), ReqInsErrEnum.TARGET_LINE_ISSUED.getDesc() + detailEntity.toString());
                }
            }
        }
    }

    /**
     * 部分混料不允许变更, 即替换数量不等于原数量
     * @since 2020/5/28 11:04 上午
     * @author xueyuting
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
                    throw new ServerException(ReqInsErrEnum.PART_MIX.getCode(), ReqInsErrEnum.PART_MIX.getDesc() + detailEntity.toString());
                }
            }
        }
    }

    /**
     * 存在未执行的前置指令不允许变更
     * @since 2020/6/1 2:28 下午
     * @author xueyuting
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
            throw new ServerException(ReqInsErrEnum.EXISTS_PRE_INS.getCode(), ReqInsErrEnum.EXISTS_PRE_INS.getDesc() + String.join(",", billNoList));
        }
    }

    /**
     * 校验投料指令是否已执行
     * @since 2020/6/9 8:23 下午
     * @author xueyuting
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
