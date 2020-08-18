package com.cvte.scm.wip.app.req.line;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.error.ReqLineErrEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/24 14:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class ReqLineSyncApplication {

    private static final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private WipReqLineRepository linesRepository;
    private WipReqHeaderRepository headerRepository;
    private DataSourceTransactionManager pgTransactionManager;
    private TransactionTemplate transactionTemplate;

    public ReqLineSyncApplication(WipReqLineRepository linesRepository
            , WipReqHeaderRepository headerRepository
            , @Qualifier("pgTransactionManager") DataSourceTransactionManager pgTransactionManager
            , TransactionTemplate transactionTemplate) {
        this.linesRepository = linesRepository;
        this.headerRepository = headerRepository;
        this.pgTransactionManager = pgTransactionManager;
        this.transactionTemplate = transactionTemplate;
    }

    public String doAction(Map<String, Object> map) {

        String organizationIdStr = (String)map.get("organizationId");
        if (StringUtils.isBlank(organizationIdStr)) {
            throw new ServerException(ReqLineErrEnum.ORG_NON_NULL.getCode(), ReqLineErrEnum.ORG_NON_NULL.getDesc());
        }
        List<Integer> organizationIdList = Arrays.stream(organizationIdStr.split(",")).map(Integer::parseInt).collect(Collectors.toList());

        String factoryId = (String)map.get("factoryId");
        Object lockFactory;
        if (StringUtils.isNotBlank(factoryId)) {
            readWriteLock.readLock().lock();
            lockFactory = (factoryId).intern();
        } else {
            // 定时任务调用时工厂为空
            readWriteLock.writeLock().lock();
            lockFactory = new Object();
        }

        List<String> sourceIdList = new ArrayList<>();
        try {
            synchronized (lockFactory) {
                List<WipReqHeaderEntity> addedHeaderData = headerRepository.selectAddedData(organizationIdList, factoryId);

                if (ListUtil.notEmpty(addedHeaderData)) {
                    transactionTemplate.setTransactionManager(pgTransactionManager);
                    transactionTemplate.execute(status -> {
                        // 投料单头增量导入
                        headerRepository.batchInsert(addedHeaderData);
                        sourceIdList.addAll(addedHeaderData.stream().map(WipReqHeaderEntity::getSourceId).collect(Collectors.toList()));
                        // 投料单行增量导入
                        linesRepository.writeIncrementalData(sourceIdList, organizationIdList);

                        return null;
                    });
                }

                // 补充更改单自动生成的投料行
                linesRepository.writeLackLines(sourceIdList, organizationIdList);
            }
        } finally {
            if (StringUtils.isNotBlank(factoryId)) {
                readWriteLock.readLock().unlock();
            } else {
                readWriteLock.writeLock().unlock();
            }
        }

        return ListUtil.notEmpty(sourceIdList) ? String.format("来源工单ID为%s的投料单同步成功", String.join(",", sourceIdList)) : "";
    }

}
