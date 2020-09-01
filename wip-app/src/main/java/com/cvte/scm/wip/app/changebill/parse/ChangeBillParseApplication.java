package com.cvte.scm.wip.app.changebill.parse;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.common.utils.DateUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.service.ChangeBillSyncFailedDomainService;
import com.cvte.scm.wip.domain.core.changebill.service.SourceChangeBillService;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import com.cvte.scm.wip.domain.core.requirement.service.GenerateReqInsDomainService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
  *
  * @author  : xueyuting
  * @since    : 2020/5/21 17:11
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ChangeBillParseApplication {

    private static final String READ_WRITE_LOCK_NAME = "changeBillSyncReadWriteLock";
    private static final String REENTRANT_LOCK_NAME = "changeBillReentrantSyncLock";

    private RedissonClient redissonClient;
    private SourceChangeBillService sourceChangeBillService;
    private ChangeBillSyncFailedDomainService changeBillSyncFailedDomainService;
    private GenerateReqInsDomainService generateReqInsDomainService;

    public ChangeBillParseApplication(RedissonClient redissonClient, SourceChangeBillService sourceChangeBillService, ChangeBillSyncFailedDomainService changeBillSyncFailedDomainService, GenerateReqInsDomainService generateReqInsDomainService) {
        this.redissonClient = redissonClient;
        this.sourceChangeBillService = sourceChangeBillService;
        this.changeBillSyncFailedDomainService = changeBillSyncFailedDomainService;
        this.generateReqInsDomainService = generateReqInsDomainService;
    }

    public String doAction(ChangeBillQueryVO queryVO) {
        if (Objects.isNull(queryVO.getLastUpdDate())) {
            queryVO.setLastUpdDate(DateUtils.getMinutesBeforeTime(LocalDateTime.now(), 10));
        }

        if (StringUtils.isBlank(queryVO.getOrganizationId())) {
            throw new ParamsIncorrectException("组织ID不可为空");
        }

        List<String> syncedBillNoList = new ArrayList<>();

        String[] organizationIdArr = queryVO.getOrganizationId().split(",");
        for (String splitOrganizationId : organizationIdArr) {
            splitOrganizationId = splitOrganizationId.trim();
            queryVO.setOrganizationId(splitOrganizationId);

            String factoryId = queryVO.getFactoryId();
            // 为每个组织分配读写锁
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(READ_WRITE_LOCK_NAME + "_" + splitOrganizationId);
            RLock lock;
            if (StringUtils.isNotBlank(factoryId)) {
                readWriteLock.readLock().lock();
                lock = redissonClient.getLock(REENTRANT_LOCK_NAME + "_" + factoryId);
            } else {
                // 定时任务调用时工厂为空
                readWriteLock.writeLock().lock();
                lock = null;
            }

            // 接口和定时任务可能同时调用, 为避免并发引起的数据问题, 按业务组织加锁。 intern将字符串写入常量池, 保证每次拿到的是业务组织的同一个内存对象
            try {
                if (null != lock) {
                    lock.lock();
                }

                List<ChangeBillBuildVO> changeBillBuildVOList;

                // 获取EBS更改单
                changeBillBuildVOList = sourceChangeBillService.querySourceChangeBill(queryVO);

                // 把已创建更改单, 但是创建指令失败的单据加进来
                changeBillBuildVOList = changeBillSyncFailedDomainService.addSyncFailedBills(changeBillBuildVOList, queryVO.getFactoryId());

                if (ListUtil.empty(changeBillBuildVOList)) {
                    continue;
                }

                for (ChangeBillBuildVO changeBillBuildVO : changeBillBuildVOList) {
                    ChangeBillEntity billEntity = null;
                    try {
                        // 生成更改单
                        billEntity = ChangeBillEntity.get().completeChangeBill(changeBillBuildVO);
                        // 生成指令
                        generateReqInsDomainService.parseChangeBill(billEntity);
                    } catch (RuntimeException re) {
                        if (Objects.isNull(billEntity)) {
                            billEntity = ChangeBillEntity.get();
                            billEntity.setBillId(changeBillBuildVO.getBillId())
                                    .setBillNo(changeBillBuildVO.getBillNo())
                                    .setMoId(changeBillBuildVO.getMoId());
                        }
                        billEntity.notifyEntity(re.getMessage());
                    }
                }

                syncedBillNoList.addAll(changeBillBuildVOList.stream().map(ChangeBillBuildVO::getBillNo).collect(Collectors.toList()));
            } finally {
                if (null != lock) {
                    lock.unlock();
                }
                if (StringUtils.isNotBlank(factoryId)) {
                    readWriteLock.readLock().unlock();
                } else {
                    readWriteLock.writeLock().unlock();
                }
            }
        }

        return String.join(",", syncedBillNoList);
    }

}
