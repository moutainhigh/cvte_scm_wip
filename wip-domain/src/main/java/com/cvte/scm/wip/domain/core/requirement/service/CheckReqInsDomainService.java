package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.common.enums.error.ChangeBillErrEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Map<String, List<WipReqLineEntity>> reqLineMap = new HashMap<>();
        for (ReqInsDetailEntity detailEntity : insEntity.getDetailList()) {
            WipReqLineKeyQueryVO keyQueryVO = WipReqLineKeyQueryVO.build(insEntity.getAimHeaderId(), detailEntity);
            List<WipReqLineEntity> reqLineList = lineRepository.selectValidByKey(keyQueryVO);
            if (!ChangedTypeEnum.ADD.getCode().equals(detailEntity.getInsStatus())) {
                if (ListUtil.empty(reqLineList)) {
                    throw new ServerException(ChangeBillErrEnum.TARGET_LINE_INVALID.getCode(), ChangeBillErrEnum.TARGET_LINE_INVALID.getDesc() + keyQueryVO.toString());
                }
                reqLineMap.put(detailEntity.getInsDetailId(), reqLineList);
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
            if (ChangedTypeEnum.ADD.getCode().equals(detailEntity.getInsStatus())) {
                // 新增类型无需校验
                continue;
            }
            WipReqLineKeyQueryVO keyQueryVO = WipReqLineKeyQueryVO.build(insEntity.getAimHeaderId(), detailEntity);
            List<WipReqLineEntity> reqLineList = reqLineMap.get(detailEntity.getInsDetailId());
            for (WipReqLineEntity reqLine : reqLineList) {
                if (BillStatusEnum.ISSUED.getCode().equals(reqLine.getLineStatus())) {
                    throw new ServerException(ChangeBillErrEnum.TARGET_LINE_ISSUED.getCode(), ChangeBillErrEnum.TARGET_LINE_ISSUED.getDesc() + keyQueryVO.toString());
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
            if (ChangedTypeEnum.REPLACE.getCode().equals(detailEntity.getOperationType()) && Objects.nonNull(detailEntity.getItemQty())) {
                WipReqLineKeyQueryVO keyQueryVO = WipReqLineKeyQueryVO.build(insEntity.getAimHeaderId(), detailEntity);
                List<WipReqLineEntity> reqLineList = reqLineMap.get(detailEntity.getInsDetailId());
                long reqQty = reqLineList.stream().filter(line -> Objects.nonNull(line.getReqQty())).mapToLong(WipReqLineEntity::getReqQty).sum();
                long replaceQty = detailEntity.getItemQty().longValue();
                if (reqQty != replaceQty) {
                    throw new ServerException(ChangeBillErrEnum.PART_MIX.getCode(), ChangeBillErrEnum.PART_MIX.getDesc() + keyQueryVO.toString());
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
        List<ReqInsEntity> preInsEntity = insEntity.getByAimHeaderId(insEntity.getAimHeaderId(), statusList);
        if (ListUtil.notEmpty(preInsEntity)) {
            throw new ServerException(ChangeBillErrEnum.EXISTS_PRE_INS.getCode(), ChangeBillErrEnum.EXISTS_PRE_INS.getDesc() + insEntity.getAimReqLotNo());
        }
    }

}
