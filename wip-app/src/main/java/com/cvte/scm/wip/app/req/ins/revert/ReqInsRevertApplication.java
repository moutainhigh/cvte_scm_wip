package com.cvte.scm.wip.app.req.ins.revert;

import com.cvte.csb.core.exception.ServerException;
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
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/13 14:21
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ReqInsRevertApplication {

    private CheckReqInsDomainService checkReqInsDomainService;
    private DataSourceTransactionManager pgTransactionManager;
    private TransactionTemplate transactionTemplate;
    private WipReqLineService wipReqLineService;

    public ReqInsRevertApplication(CheckReqInsDomainService checkReqInsDomainService, DataSourceTransactionManager pgTransactionManager, TransactionTemplate transactionTemplate, WipReqLineService wipReqLineService) {
        this.checkReqInsDomainService = checkReqInsDomainService;
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
        this.wipReqLineService = wipReqLineService;
    }

    public String doAction(String[] insHeaderIds) {
        List<ReqInsEntity> insHeaderList = new ArrayList<>();
        for (String insHeaderId : insHeaderIds) {
            ReqInsEntity insHeader = ReqInsEntity.get().getByKey(insHeaderId);
            if (!ProcessingStatusEnum.SOLVED.getCode().equals(insHeader.getStatus())) {
                throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc() + String.format("仅支持撤回已处理的更改单, %s", insHeaderId));
            }
            insHeader.getDetailById().removeIf(detail -> StatusEnum.CLOSE.getCode().equals(detail.getInsStatus()));
            insHeaderList.add(insHeader);
        }
        // TODO 按确认日期倒序

        for (ReqInsEntity insHeader : insHeaderList) {
            // 复制指令
            ReqInsEntity undoInsHeader = ReqInsEntity.get();
            undoInsHeader.setDetailList(new ArrayList<>(insHeader.getDetailList().size()));
            BeanUtils.copyProperties(insHeader, undoInsHeader);
            List<ReqInsDetailEntity> undoDetailList = new ArrayList<>();
            for (ReqInsDetailEntity detail : insHeader.getDetailList()) {
                ReqInsDetailEntity undoInsDetail = ReqInsDetailEntity.get();
                BeanUtils.copyProperties(detail, undoInsDetail);
                undoDetailList.add(undoInsDetail);
            }
            undoInsHeader.setDetailList(undoDetailList);

            // 还原物料
            undoInsHeader.revertIns();

            Map<String, List<WipReqLineEntity>> reqLineMap = checkReqInsDomainService.validAndGetLine(undoInsHeader);
            checkReqInsDomainService.checkLineStatus(undoInsHeader, reqLineMap);

            List<WipReqLineEntity> reqLineList = undoInsHeader.parse(reqLineMap);

            transactionTemplate.setTransactionManager(pgTransactionManager);
            transactionTemplate.execute(status -> {
                wipReqLineService.executeByChangeBill(reqLineList, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());
                return null;
            });

            // 还原状态
            insHeader.revertStatus();
        }

        return "";
    }

}
