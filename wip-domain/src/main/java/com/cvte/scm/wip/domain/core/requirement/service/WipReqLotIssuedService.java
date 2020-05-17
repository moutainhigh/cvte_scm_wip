package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLogEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLogRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-01-17
 */
@Slf4j
@Service
@Transactional
public class WipReqLotIssuedService {

    private WipReqLotIssuedRepository wipReqLotIssuedRepository;
    private WipReqLineRepository wipReqLineRepository;
    private WipReqLogRepository wipReqLogRepository;

    public WipReqLotIssuedService(WipReqLotIssuedRepository wipReqLotIssuedRepository, WipReqLineRepository wipReqLineRepository, WipReqLogRepository wipReqLogRepository) {
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
        this.wipReqLineRepository = wipReqLineRepository;
        this.wipReqLogRepository = wipReqLogRepository;
    }

    public void add(WipReqLotIssuedEntity wipReqLotIssued) {
        this.verifyIssuedQty(wipReqLotIssued);
        // 初始化信息
        EntityUtils.writeStdCrtInfoToEntity(wipReqLotIssued, CurrentContext.getCurrentOperatingUser().getId());
        if (StringUtils.isBlank(wipReqLotIssued.getId())) {
            // 新增
            wipReqLotIssued.setId(UUIDUtils.get32UUID())
                    .setStatus(StatusEnum.NORMAL.getCode());
            wipReqLotIssuedRepository.insert(wipReqLotIssued);
        }
        log.info("新增领料批次成功,id = {}", wipReqLotIssued.getId());
        this.generateLog(wipReqLotIssued, wipReqLotIssued, ChangedTypeEnum.ISSUED_ADD.getCode());
    }

    public void invalid(List<String> idList) {
        wipReqLotIssuedRepository.invalidById(idList);
        for (String id : idList) {
            WipReqLotIssuedEntity invalidedEntity = wipReqLotIssuedRepository.selectById(id);
            this.generateLog(invalidedEntity, invalidedEntity, ChangedTypeEnum.ISSUED_INVALID.getCode());
        }
        log.info("失效领料批次成功,idList = {}", idList.toString());
    }

    /**
     * 校验领料批次数量
     *
     * @param wipReqLotIssued 领料数据
     * @author xueyuting
     * @since 2020/2/04 3:07 下午
     */
    private void verifyIssuedQty(WipReqLotIssuedEntity wipReqLotIssued) {
        // 已领料数据
        WipReqLotIssuedEntity queryLotEntity = new WipReqLotIssuedEntity().setHeaderId(wipReqLotIssued.getHeaderId())
                .setOrganizationId(wipReqLotIssued.getOrganizationId()).setItemNo(wipReqLotIssued.getItemNo()).setWkpNo(wipReqLotIssued.getWkpNo()).setStatus(StatusEnum.NORMAL.getCode());
        List<WipReqLotIssuedEntity> lotIssuedList = wipReqLotIssuedRepository.selectList(queryLotEntity);
        if (lotIssuedList == null) {
            lotIssuedList = new ArrayList<>();
        }
        // 新增:直接插入list;保存:更新数据
        if (StringUtils.isEmpty(wipReqLotIssued.getId())) {
            lotIssuedList.add(wipReqLotIssued);
        } else {
            lotIssuedList.forEach(issued -> {
                if (wipReqLotIssued.getId().equals(issued.getId())) {
                    issued.setIssuedQty(wipReqLotIssued.getIssuedQty());
                }
            });
        }
        // 需求数据
        WipReqLineEntity queryLinesEntity = new WipReqLineEntity().setHeaderId(wipReqLotIssued.getHeaderId())
                .setOrganizationId(wipReqLotIssued.getOrganizationId()).setItemNo(wipReqLotIssued.getItemNo()).setWkpNo(wipReqLotIssued.getWkpNo());
        List<WipReqLineEntity> reqLinesList = wipReqLineRepository.selectList(queryLinesEntity);
        int totalIssuedQty = lotIssuedList.stream().mapToInt(WipReqLotIssuedEntity::getIssuedQty).sum();
        int totalReqQty = reqLinesList.stream().mapToInt(WipReqLineEntity::getReqQty).sum();
        if (totalIssuedQty > totalReqQty) {
            log.info("领料数量大于需求数量,新增领料数量失败");
            throw new ParamsIncorrectException("领料数量大于需求数量");
        }
    }

    private void generateLog(WipReqLotIssuedEntity beforeEntity, WipReqLotIssuedEntity afterEntity, String operationType) {
        WipReqLogEntity wipReqLog = new WipReqLogEntity();
        wipReqLog.setLineId("lotIssuedId_" + afterEntity.getId())
                .setLogId(UUIDUtils.get32UUID())
                .setBeforeItemNo(beforeEntity.getItemNo())
                .setAfterItemNo(afterEntity.getItemNo())
                .setBeforeWkpNo(beforeEntity.getWkpNo())
                .setAfterWkpNo(afterEntity.getWkpNo())
                .setUpdUser(CurrentContext.getCurrentOperatingUser().getId())
                .setUpdDate(new Date())
                .setOperationType(operationType)
                .setHeaderId(afterEntity.getHeaderId())
                .setBeforeItemQty(beforeEntity.getIssuedQty())
                .setAfterItemQty(afterEntity.getIssuedQty())
                .setMtrLotNo(afterEntity.getMtrLotNo())
                .setRmk01(afterEntity.getId());
        wipReqLogRepository.insert(wipReqLog);
        log.info("保存更改日志成功, log_id = {}", wipReqLog.getLogId());
    }
}
