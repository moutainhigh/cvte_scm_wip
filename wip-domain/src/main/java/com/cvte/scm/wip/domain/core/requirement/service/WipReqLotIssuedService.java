package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.YoNEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLogEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLogRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLotProcessVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
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
public class WipReqLotIssuedService {

    private WipReqLotIssuedRepository wipReqLotIssuedRepository;
    private WipReqLineRepository wipReqLineRepository;
    private WipReqLogRepository wipReqLogRepository;
    private CheckReqLineService checkReqLineService;

    public WipReqLotIssuedService(WipReqLotIssuedRepository wipReqLotIssuedRepository, WipReqLineRepository wipReqLineRepository, WipReqLogRepository wipReqLogRepository, CheckReqLineService checkReqLineService) {
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
        this.wipReqLineRepository = wipReqLineRepository;
        this.wipReqLogRepository = wipReqLogRepository;
        this.checkReqLineService = checkReqLineService;
    }

    public WipReqLotIssuedEntity selectById(String id) {
        return wipReqLotIssuedRepository.selectById(id);
    }

    public List<WipReqLotIssuedEntity> selectLockedByKey(WipReqLotProcessVO vo) {
        WipReqLotIssuedEntity queryEntity = new WipReqLotIssuedEntity();
        queryEntity.setOrganizationId(vo.getOrganizationId())
                .setItemNo(vo.getItemNo())
                .setMtrLotNo(vo.getMtrLotNo())
                .setLockStatus(YoNEnum.Y.getCode())
                .setStatus(StatusEnum.NORMAL.getCode());
        return wipReqLotIssuedRepository.selectList(queryEntity);
    }

    public void save(WipReqLotIssuedEntity wipReqLotIssued) {
        checkReqLineService.checkItemExists(wipReqLotIssued.getOrganizationId(), wipReqLotIssued.getHeaderId(), wipReqLotIssued.getWkpNo(), wipReqLotIssued.getItemNo());
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

    public void changeLockStatus(List<String> idList) {
        List<WipReqLotIssuedEntity> lotIssuedEntityList = wipReqLotIssuedRepository.selectById(idList);
        if (idList.size() != lotIssuedEntityList.size()) {
            List<String> notExistsIdList = lotIssuedEntityList.stream().map(WipReqLotIssuedEntity::getId).filter(idList::contains).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            for (String id : notExistsIdList) {
                sb.append(id).append("/");
            }
            sb.append("对应的领料批次不存在");
            throw new ParamsIncorrectException(sb.toString());
        }
        for (WipReqLotIssuedEntity lotIssuedEntity : lotIssuedEntityList) {
            if (YoNEnum.Y.getCode().equals(lotIssuedEntity.getLockStatus())) {
                lotIssuedEntity.setLockStatus(YoNEnum.N.getCode());
            } else {
                lotIssuedEntity.setLockStatus(YoNEnum.Y.getCode());
            }
        }
        for (WipReqLotIssuedEntity lotIssuedEntity : lotIssuedEntityList) {
            EntityUtils.writeStdUpdInfoToEntity(lotIssuedEntity, EntityUtils.getWipUserId());
            wipReqLotIssuedRepository.update(lotIssuedEntity);
        }
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
        long oldTotalLotIssuedQty = lotIssuedList.stream().mapToLong(WipReqLotIssuedEntity::getIssuedQty).sum();

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

        long totalReqQty = reqLinesList.stream().mapToLong(WipReqLineEntity::getReqQty).sum();
        if (Objects.isNull(wipReqLotIssued.getIssuedQty())) {
            wipReqLotIssued.setIssuedQty(totalReqQty - oldTotalLotIssuedQty);
        } else {
            long totalIssuedQty = lotIssuedList.stream().mapToLong(WipReqLotIssuedEntity::getIssuedQty).sum();
            if (totalIssuedQty > totalReqQty) {
                log.info("领料数量大于需求数量,新增领料数量失败");
                throw new ParamsIncorrectException("领料数量大于需求数量");
            }
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
                .setBeforeItemQty(beforeEntity.getIssuedQty().intValue())
                .setAfterItemQty(afterEntity.getIssuedQty().intValue())
                .setMtrLotNo(afterEntity.getMtrLotNo())
                .setRmk01(afterEntity.getId());
        wipReqLogRepository.insert(wipReqLog);
        log.info("保存更改日志成功, log_id = {}", wipReqLog.getLogId());
    }
}
