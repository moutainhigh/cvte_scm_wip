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
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-01-17
 */
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipReqLotIssuedService {

    private WipReqLotIssuedRepository wipReqLotIssuedRepository;
    private WipReqLineRepository wipReqLineRepository;
    private WipReqLogRepository wipReqLogRepository;

    public WipReqLotIssuedService(WipReqLotIssuedRepository wipReqLotIssuedRepository, WipReqLineRepository wipReqLineRepository, WipReqLogRepository wipReqLogRepository) {
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
        this.wipReqLineRepository = wipReqLineRepository;
        this.wipReqLogRepository = wipReqLogRepository;
    }

    public WipReqLotIssuedEntity selectById(String id) {
        return wipReqLotIssuedRepository.selectById(id);
    }

    public void add(WipReqLotIssuedEntity wipReqLotIssued) {
        this.verifyIssuedQty(wipReqLotIssued);
        if (StringUtils.isBlank(wipReqLotIssued.getId())) {
            // 新增
            wipReqLotIssued.setId(UUIDUtils.get32UUID())
                    .setStatus(StatusEnum.NORMAL.getCode());
            wipReqLotIssuedRepository.insert(wipReqLotIssued);
        } else {
            // 更新
            wipReqLotIssuedRepository.update(wipReqLotIssued);
        }
    }

    public void invalid(String id) {
        wipReqLotIssuedRepository.invalidById(id);
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

        // 替换旧的
        Iterator<WipReqLotIssuedEntity> iterator = lotIssuedList.iterator();
        while (iterator.hasNext()) {
            WipReqLotIssuedEntity lotIssued = iterator.next();
            if (lotIssued.getMtrLotNo().equals(wipReqLotIssued.getMtrLotNo())) {
                // 更新ID, 用于保存
                wipReqLotIssued.setId(lotIssued.getId());
                iterator.remove();
            }
        }
        lotIssuedList.add(wipReqLotIssued);

        // 需求数据
        WipReqLineKeyQueryVO keyQueryVO = new WipReqLineKeyQueryVO();
        keyQueryVO.setHeaderId(wipReqLotIssued.getHeaderId())
                .setOrganizationId(wipReqLotIssued.getOrganizationId())
                .setItemNo(wipReqLotIssued.getItemNo())
                .setWkpNo(wipReqLotIssued.getWkpNo());
        Set<BillStatusEnum> statusList = BillStatusEnum.getValidStatusSet();
        List<WipReqLineEntity> reqLinesList = wipReqLineRepository.selectValidByKey(keyQueryVO, statusList);

        int totalIssuedQty = lotIssuedList.stream().mapToInt(WipReqLotIssuedEntity::getIssuedQty).sum();
        int totalReqQty = reqLinesList.stream().mapToInt(WipReqLineEntity::getReqQty).sum();
        if (totalIssuedQty > totalReqQty) {
            log.info("领料数量大于需求数量,新增领料数量失败");
            throw new ParamsIncorrectException("领料数量大于需求数量");
        }
    }

    public void generateIssuedLog(WipReqLotIssuedEntity beforeEntity, WipReqLotIssuedEntity afterEntity, String operationType) {
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
