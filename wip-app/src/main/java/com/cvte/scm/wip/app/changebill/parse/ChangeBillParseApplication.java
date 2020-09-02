package com.cvte.scm.wip.app.changebill.parse;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
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
        CountDownLatch countDownLatch = new CountDownLatch(3);
        for (String splitOrganizationId : organizationIdArr) {
            ChangeBillQueryVO dupQueryVO = new ChangeBillQueryVO();
            BeanUtils.copyProperties(queryVO, dupQueryVO);
            splitOrganizationId = splitOrganizationId.trim();
            dupQueryVO.setOrganizationId(splitOrganizationId);

            ChangeBillParseTask parseTask = new ChangeBillParseTask(countDownLatch, dupQueryVO, syncedBillNoList);
            Thread taskThread = new Thread(parseTask);
            taskThread.start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            String msg = "等待子线程时发生未知异常, msg = " + e.getMessage();
            syncedBillNoList.add(msg);
            log.error(msg);
        }
        return String.join(",", syncedBillNoList);
    }

    private class ChangeBillParseTask implements Runnable {

        private CountDownLatch countDownLatch;
        private ChangeBillQueryVO queryVO;
        private List<String> syncedBillNoList;

        ChangeBillParseTask(CountDownLatch countDownLatch, ChangeBillQueryVO queryVO, List<String> syncedBillNoList) {
            this.countDownLatch = countDownLatch;
            this.queryVO = queryVO;
            this.syncedBillNoList = syncedBillNoList;
        }

        @Override
        public void run() {
            String factoryId = queryVO.getFactoryId();
            // 为每个组织分配读写锁
            RReadWriteLock readWriteLock;
            try {
                readWriteLock = redissonClient.getReadWriteLock(READ_WRITE_LOCK_NAME + "_" + queryVO.getOrganizationId());
            } catch (RuntimeException re) {
                String msg = queryVO.getOrganizationId() + "组织获取读写锁时异常, msg = " + re.getMessage();
                syncedBillNoList.add(msg);
                log.error(msg);
                countDownLatch.countDown();
                return;
            }
            if (Objects.isNull(readWriteLock)) {
                String msg = queryVO.getOrganizationId() + "组织获取的读写锁为空";
                syncedBillNoList.add(msg);
                log.error(msg);
                countDownLatch.countDown();
                return;
            }

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
                changeBillBuildVOList = changeBillSyncFailedDomainService.addSyncFailedBills(changeBillBuildVOList, queryVO);

                if (ListUtil.empty(changeBillBuildVOList)) {
                    return;
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
            } catch (RuntimeException re) {
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append(queryVO.getOrganizationId()).append("组织");
                if (StringUtils.isNotBlank(queryVO.getFactoryId())) {
                    msgBuilder.append(",").append(queryVO.getFactoryId()).append("工厂");
                }
                msgBuilder.append("同步时发生异常, msg = ").append(re.getMessage());
                syncedBillNoList.add(msgBuilder.toString());
                log.error(msgBuilder.toString());
            } finally {
                if (null != lock) {
                    lock.unlock();
                }
                if (StringUtils.isNotBlank(factoryId)) {
                    readWriteLock.readLock().unlock();
                } else {
                    readWriteLock.writeLock().unlock();
                }
                countDownLatch.countDown();
            }
        }

    }

}
