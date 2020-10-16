package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.enums.YoNEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotProcessRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedLockTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 10:46
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipReqLotProcessService {

    private ScmItemService itemService;
    private WipReqLotIssuedService wipReqLotIssuedService;
    private WipReqLotProcessRepository wipReqLotProcessRepository;

    public WipReqLotProcessService(ScmItemService itemService, WipReqLotIssuedService wipReqLotIssuedService, WipReqLotProcessRepository wipReqLotProcessRepository) {
        this.itemService = itemService;
        this.wipReqLotIssuedService = wipReqLotIssuedService;
        this.wipReqLotProcessRepository = wipReqLotProcessRepository;
    }

    public void createAndProcess(List<WipReqLotProcessEntity> wipReqLotProcessList) {
        create(wipReqLotProcessList);
        process(wipReqLotProcessList);
    }

    public void create(List<WipReqLotProcessEntity> wipReqLotProcessList) {
        for (WipReqLotProcessEntity itemLotProcess : wipReqLotProcessList) {
            if (StringUtils.isBlank(itemLotProcess.getItemNo()) && StringUtils.isNotBlank(itemLotProcess.getItemId())) {
                itemLotProcess.setItemNo(itemService.getItemNo(itemLotProcess.getItemId()));
            }
            itemLotProcess.setId(UUIDUtils.get32UUID())
                    .setProcessStatus(ProcessingStatusEnum.PENDING.getCode());
            EntityUtils.writeStdCrtInfoToEntity(itemLotProcess, Optional.ofNullable(itemLotProcess.getOptUser()).orElse(EntityUtils.getWipUserId()));
        }
        wipReqLotProcessRepository.insertList(wipReqLotProcessList);
    }

    public void process(List<WipReqLotProcessEntity> wipReqLotProcessList) {
        // 按物料+工序聚合
        Map<String, List<WipReqLotProcessEntity>> itemLotProcessMap = wipReqLotProcessList.stream().collect(Collectors.groupingBy(WipReqLotProcessEntity::getItemKey));
        for (Map.Entry<String, List<WipReqLotProcessEntity>> itemLotProcessEntry : itemLotProcessMap.entrySet()) {
            List<WipReqLotProcessEntity> itemLotProcessList = itemLotProcessEntry.getValue();
            List<WipReqLotIssuedEntity> itemLotIssuedList = new ArrayList<>();
            for (WipReqLotProcessEntity itemLotProcess : itemLotProcessList) {
                // 生成投料批次实体
                WipReqLotIssuedEntity itemLotIssued = new WipReqLotIssuedEntity();
                itemLotIssued.setOrganizationId(itemLotProcess.getOrganizationId())
                        .setHeaderId(itemLotProcess.getWipEntityId())
                        .setItemNo(itemLotProcess.getItemNo())
                        .setWkpNo(itemLotProcess.getWkpNo())
                        .setMtrLotNo(itemLotProcess.getMtrLotNo())
                        .setAssignQty(new BigDecimal(Optional.ofNullable(itemLotProcess.getIssuedQty()).orElse(0L).toString()))
                        .setLockStatus(YoNEnum.Y.getCode())
                        .setLockType(LotIssuedLockTypeEnum.MANUAL.getCode());
                itemLotIssuedList.add(itemLotIssued);

                itemLotProcess.setProcessStatus(ProcessingStatusEnum.SOLVED.getCode());
            }

            try {
                // 保存投料批次
                wipReqLotIssuedService.saveAll(itemLotIssuedList);
            } catch (RuntimeException re) {
                for (WipReqLotProcessEntity itemLotProcess : itemLotProcessList) {
                    // 记录异常信息
                    itemLotProcess.setProcessStatus(ProcessingStatusEnum.EXCEPTION.getCode())
                            .setProcessResult(re.getMessage());
                }
            }
        }
        for (WipReqLotProcessEntity updateEntity : wipReqLotProcessList) {
            EntityUtils.writeStdUpdInfoToEntity(updateEntity, Optional.ofNullable(updateEntity.getOptUser()).orElse(EntityUtils.getWipUserId()));
        }
        wipReqLotProcessRepository.updateList(wipReqLotProcessList);
    }

}
