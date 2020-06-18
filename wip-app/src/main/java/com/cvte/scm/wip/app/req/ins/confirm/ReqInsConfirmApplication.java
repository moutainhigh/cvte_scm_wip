package com.cvte.scm.wip.app.req.ins.confirm;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.service.CheckReqInsDomainService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLineService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
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

    private CheckReqInsDomainService checkReqInsDomainService;
    private WipReqLineService wipReqLineService;
    private DataSourceTransactionManager pgTransactionManager;
    private TransactionTemplate transactionTemplate;

    public ReqInsConfirmApplication(CheckReqInsDomainService checkReqInsDomainService, WipReqLineService wipReqLineService
            , @Qualifier("pgTransactionManager")DataSourceTransactionManager pgTransactionManager
            , TransactionTemplate transactionTemplate) {
        this.checkReqInsDomainService = checkReqInsDomainService;
        this.wipReqLineService = wipReqLineService;
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public String doAction(String[] insHeaderIds) {

        List<ReqInsEntity> insHeaderList = new ArrayList<>();
        for (String insHeaderId : insHeaderIds) {
            ReqInsEntity insHeader = ReqInsEntity.get().getByKey(insHeaderId);
            if (Objects.isNull(insHeader)) {
                throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc() + String.format("ID为%s的指令不存在", insHeaderId));
            }
            // 去掉已作废的明细
            insHeader.getDetailById().removeIf(detail -> StatusEnum.CLOSE.getCode().equals(detail.getInsStatus()));
            insHeaderList.add(insHeader);
        }
        insHeaderList.sort((Comparator.comparing(ReqInsEntity::getEnableDate)));

        for (ReqInsEntity insHeader : insHeaderList) {

            try {
                checkReqInsDomainService.checkInsProcessed(insHeader);
            } catch (ServerException se) {
                // 跳过已执行的指令
                log.warn(se.getMessage());
                continue;
            }

            Map<String, List<WipReqLineEntity>> reqLineMap;
            try {
                checkReqInsDomainService.checkPreInsExists(insHeader);
            } catch (RuntimeException re) {
                insHeader.processFailed("校验失败," + re.getMessage());
                throw re;
            }

            try {
                reqLineMap = checkReqInsDomainService.validAndGetLine(insHeader);
                checkReqInsDomainService.checkLineStatus(insHeader, reqLineMap);
            } catch (RuntimeException re) {
                String headerErrMsg = insHeader.groupDetailExecuteResult();
                insHeader.processFailed("校验失败," + headerErrMsg);
                throw re;
            }

            List<WipReqLineEntity> reqLineList;
            try {
                reqLineList = insHeader.parse(reqLineMap);
            } catch (RuntimeException re) {
                String headerErrMsg = insHeader.groupDetailExecuteResult();
                insHeader.processFailed("解析失败," + headerErrMsg);
                throw re;
            }

            transactionTemplate.setTransactionManager(pgTransactionManager);
            try {
                transactionTemplate.execute(status -> {
                    wipReqLineService.executeByChangeBill(reqLineList, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());
                    return null;
                });
            } catch (RuntimeException re) {
                this.lineErrToInsErr(reqLineList, insHeader.getDetailList());
                insHeader.processFailed("执行失败," + this.groupExecuteErr(reqLineList));
                throw re;
            }

            insHeader.processSuccess();

        }

        return null;
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
