package com.cvte.scm.wip.app.req.ins.confirm;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.enums.ExecutionResultEnum;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.service.CheckReqInsDomainService;
import com.cvte.scm.wip.domain.core.requirement.service.QueryReqLineService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLineService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/2 11:02
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ReqInsConfirmApplication implements Application<String[], String> {

    private static final String VALIDATE_FAILED = "校验失败,";
    private static final String PARSE_FAILED = "解析失败,";
    private static final String EXECUTE_FAILED = "执行失败,";

    private CheckReqInsDomainService checkReqInsDomainService;
    private WipReqLineService wipReqLineService;
    private DataSourceTransactionManager pgTransactionManager;
    private TransactionTemplate transactionTemplate;

    public ReqInsConfirmApplication(CheckReqInsDomainService checkReqInsDomainService, WipReqLineService wipReqLineService
            , @Qualifier("pgTransactionManager") DataSourceTransactionManager pgTransactionManager
            , TransactionTemplate transactionTemplate) {
        this.checkReqInsDomainService = checkReqInsDomainService;
        this.wipReqLineService = wipReqLineService;
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public String doAction(String[] insHeaderIds) {
        List<ReqInsConfirmResultDTO> confirmResultList = new ArrayList<>();

        List<ReqInsEntity> insHeaderList = new ArrayList<>();
        for (String insHeaderId : insHeaderIds) {
            if (StringUtils.isBlank(insHeaderId)) {
                confirmResultList.add(new ReqInsConfirmResultDTO(null, ExecutionResultEnum.SKIP, null));
                continue;
            }
            ReqInsEntity insHeader = ReqInsEntity.get().getByKey(insHeaderId);
            if (Objects.isNull(insHeader)) {
                confirmResultList.add(new ReqInsConfirmResultDTO(null, ExecutionResultEnum.FAILED, VALIDATE_FAILED + String.format("ID为%s的指令不存在", insHeaderId)));
                continue;
            }
            // 去掉已作废的明细
            insHeader.getDetailById().removeIf(detail -> ProcessingStatusEnum.CLOSE.getCode().equals(detail.getInsStatus()));
            insHeaderList.add(insHeader);
        }
        insHeaderList.sort((Comparator.comparing(ReqInsEntity::getEnableDate)));

        for (ReqInsEntity insHeader : insHeaderList) {

            try {
                checkReqInsDomainService.checkInsProcessed(insHeader);
            } catch (ServerException se) {
                // 跳过已执行的指令
                confirmResultList.add(new ReqInsConfirmResultDTO(insHeader, ExecutionResultEnum.SKIP, null));
                continue;
            }

            try {
                // 校验是否有前置更改单
                checkReqInsDomainService.checkPreInsExists(insHeader);
            } catch (RuntimeException re) {
                insHeader.processFailed(VALIDATE_FAILED + re.getMessage());
                confirmResultList.add(new ReqInsConfirmResultDTO(insHeader, ExecutionResultEnum.FAILED, VALIDATE_FAILED + re.getMessage()));
                continue;
            }

            Map<String, List<WipReqLineEntity>> reqLineMap;
            try {
                // 获取指令需处理的投料行
                reqLineMap = checkReqInsDomainService.validAndGetLine(insHeader);
                // 校验投料行状态
                checkReqInsDomainService.checkLineStatus(insHeader, reqLineMap);
            } catch (RuntimeException re) {
                String headerErrMsg = insHeader.groupDetailExecuteResult();
                insHeader.processFailed(VALIDATE_FAILED + headerErrMsg);
                confirmResultList.add(new ReqInsConfirmResultDTO(insHeader, ExecutionResultEnum.FAILED, VALIDATE_FAILED + re.getMessage()));
                continue;
            }

            List<WipReqLineEntity> reqLineList;
            try {
                // 解析指令
                reqLineList = insHeader.parse(reqLineMap);
            } catch (RuntimeException re) {
                String headerErrMsg = insHeader.groupDetailExecuteResult();
                insHeader.processFailed(PARSE_FAILED + headerErrMsg);
                confirmResultList.add(new ReqInsConfirmResultDTO(insHeader, ExecutionResultEnum.FAILED, PARSE_FAILED + re.getMessage()));
                continue;
            }

            transactionTemplate.setTransactionManager(pgTransactionManager);
            try {
                // 执行变更
                transactionTemplate.execute(status -> {
                    wipReqLineService.executeByChangeBill(reqLineList, ChangedTypeEnum.EXECUTE, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());
                    return null;
                });
            } catch (RuntimeException re) {
                this.lineErrToInsErr(reqLineList, insHeader.getDetailList());
                String errMsg = this.groupExecuteErr(reqLineList);
                if (StringUtils.isBlank(errMsg)) {
                    errMsg = re.getMessage();
                }
                insHeader.processFailed(EXECUTE_FAILED + errMsg);
                confirmResultList.add(new ReqInsConfirmResultDTO(insHeader, ExecutionResultEnum.FAILED, EXECUTE_FAILED + re.getMessage()));
                continue;
            }

            // 处理成功状态
            insHeader.processSuccess();
            // 通知变更成功
            insHeader.notifyEntity();
            confirmResultList.add(new ReqInsConfirmResultDTO(insHeader, ExecutionResultEnum.SUCCESS, null));

        }

        return ReqInsConfirmResultDTO.buildMsg(confirmResultList);
    }

    private void lineErrToInsErr(List<WipReqLineEntity> reqLineList, List<ReqInsDetailEntity> insDetailList) {
        // 以 ID为key 将执行结果用","集合
        Map<String, String> insErrorMap = reqLineList.stream()
                .filter(line -> StringUtils.isNotBlank(line.getExecuteResult()))
                .collect(Collectors.groupingBy(WipReqLineEntity::getInsDetailId, Collectors.mapping(WipReqLineEntity::getExecuteResult, Collectors.joining(";"))));
        for (ReqInsDetailEntity insDetail : insDetailList) {
            String errorIns = insErrorMap.get(insDetail.getInsDetailId());
            if (StringUtils.isNotBlank(errorIns)) {
                insDetail.setExecuteResult(errorIns);
            }
        }
    }

    private String groupExecuteErr(List<WipReqLineEntity> reqLineList) {
        // 以 执行结果为key 将ID用","聚合
        Map<String, String> errorInsMap = reqLineList.stream()
                .filter(line -> StringUtils.isNotBlank(line.getExecuteResult()))
                .collect(Collectors.groupingBy(WipReqLineEntity::getExecuteResult, Collectors.mapping(WipReqLineEntity::getInsDetailId, Collectors.joining(","))));
        if (errorInsMap == null || errorInsMap.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> resultEntry : errorInsMap.entrySet()) {
            sb.append(resultEntry.getKey()).append("id:").append(resultEntry.getValue()).append(";");
        }
        return sb.toString();
    }

}
