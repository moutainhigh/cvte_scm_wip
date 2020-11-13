package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.YoNEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedLockTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedOpTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private CheckReqLineService checkReqLineService;
    private CheckReqLotIssuedService checkReqLotIssuedService;
    private LotIssuedWriteBackService lotIssuedWriteBackService;

    public WipReqLotIssuedService(WipReqLotIssuedRepository wipReqLotIssuedRepository, CheckReqLineService checkReqLineService, CheckReqLotIssuedService checkReqLotIssuedService, LotIssuedWriteBackService lotIssuedWriteBackService) {
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
        this.checkReqLineService = checkReqLineService;
        this.checkReqLotIssuedService = checkReqLotIssuedService;
        this.lotIssuedWriteBackService = lotIssuedWriteBackService;
    }

    public WipReqLotIssuedEntity selectById(String id) {
        return wipReqLotIssuedRepository.selectById(id);
    }

    public void changeLockStatus(List<String> idList) {
        List<WipReqLotIssuedEntity> lotIssuedEntityList = wipReqLotIssuedRepository.selectListByIds(idList.toArray(new String[0]));
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
            lotIssuedEntity.setLockType(LotIssuedLockTypeEnum.MANUAL.getCode());
        }
        for (WipReqLotIssuedEntity lotIssuedEntity : lotIssuedEntityList) {
            EntityUtils.writeStdUpdInfoToEntity(lotIssuedEntity, EntityUtils.getWipUserId());
            wipReqLotIssuedRepository.updateSelectiveById(lotIssuedEntity);
        }
    }

    public void saveAll(List<WipReqLotIssuedEntity> reqLotIssuedList) {
        reqLotIssuedList.forEach(WipReqLotIssuedEntity::validate);

        Map<String, List<WipReqLotIssuedEntity>> reqLotIssuedMap = reqLotIssuedList.stream().collect(Collectors.groupingBy(WipReqLotIssuedEntity::getItemKey));
        for (Map.Entry<String, List<WipReqLotIssuedEntity>> reqLotIssuedEntry : reqLotIssuedMap.entrySet()) {
            List<WipReqLotIssuedEntity> itemLotIssuedList = reqLotIssuedEntry.getValue();
            WipReqLotIssuedEntity itemKeyEntity = itemLotIssuedList.get(0);
            List<WipReqLotIssuedEntity> dbLotIssuedList = getByItemKey(itemKeyEntity.getOrganizationId(), itemKeyEntity.getHeaderId(), itemKeyEntity.getItemNo(), itemKeyEntity.getWkpNo());
            List<WipReqLotIssuedEntity> insertList = new ArrayList<>();
            List<WipReqLotIssuedEntity> updateList = new ArrayList<>();
            List<WipReqLotIssuedEntity> deleteList = new ArrayList<>();
            this.classifyChanged(itemLotIssuedList, dbLotIssuedList, insertList, updateList, deleteList);
            // 校验物料存在
            checkReqLineService.checkItemExists(itemKeyEntity.getOrganizationId(), itemKeyEntity.getHeaderId(), itemKeyEntity.getWkpNo(), itemKeyEntity.getItemNo());
            // 校验批次存在
            checkReqLotIssuedService.checkLotValid(insertList);
            List<WipReqLotIssuedEntity> qtyCheckList = new ArrayList<>(insertList);
            qtyCheckList.addAll(updateList);
            // 校验投料数量
            checkReqLotIssuedService.checkIssuedQty(qtyCheckList);

            for (WipReqLotIssuedEntity insertEntity : insertList) {
                // 初始化新增信息
                insertEntity.setId(UUIDUtils.get32UUID())
                        .setStatus(StatusEnum.NORMAL.getCode());
                EntityUtils.writeStdCrtInfoToEntity(insertEntity, EntityUtils.getWipUserId());
            }
            // 逻辑删除
            for (WipReqLotIssuedEntity deleteEntity : deleteList) {
                this.checkDeletable(deleteEntity);
                deleteEntity.setStatus(StatusEnum.CLOSE.getCode());
            }
            updateList.addAll(deleteList);
            for (WipReqLotIssuedEntity updateEntity : updateList) {
                // 更新信息
                EntityUtils.writeStdUpdInfoToEntity(updateEntity, EntityUtils.getWipUserId());
            }

            wipReqLotIssuedRepository.insertList(insertList);
            wipReqLotIssuedRepository.updateList(updateList);
            insertList.forEach(insertEntity -> lotIssuedWriteBackService.writeBack(LotIssuedOpTypeEnum.ADD, insertEntity));
            deleteList.forEach(deleteEntity -> lotIssuedWriteBackService.writeBack(LotIssuedOpTypeEnum.REMOVE, deleteEntity));
        }
    }

    public void issue(List<WipReqLotIssuedEntity> reqLotIssuedList) {
        List<WipReqLotIssuedEntity> updateList = new ArrayList<>();
        List<WipReqLotIssuedEntity> insertList = new ArrayList<>();

        Map<String, List<WipReqLotIssuedEntity>> reqLotIssuedMap = reqLotIssuedList.stream().collect(Collectors.groupingBy(WipReqLotIssuedEntity::getItemKey));
        for (Map.Entry<String, List<WipReqLotIssuedEntity>> reqLotIssuedEntry : reqLotIssuedMap.entrySet()) {
            List<WipReqLotIssuedEntity> itemLotIssuedList = reqLotIssuedEntry.getValue();
            WipReqLotIssuedEntity itemKeyEntity = itemLotIssuedList.get(0);
            List<WipReqLotIssuedEntity> dbLotIssuedList = getByItemKey(itemKeyEntity.getOrganizationId(), itemKeyEntity.getHeaderId(), itemKeyEntity.getItemNo(), itemKeyEntity.getWkpNo());
            Map<String, WipReqLotIssuedEntity> dbLotIssuedMap = WipReqLotIssuedEntity.toLotMap(dbLotIssuedList);

            for (WipReqLotIssuedEntity itemLotIssued : itemLotIssuedList) {
                WipReqLotIssuedEntity dbLotIssued = dbLotIssuedMap.get(itemLotIssued.getMtrLotNo());
                if (Objects.isNull(dbLotIssued)) {
                    // 查询的投料批次为空，生成新的记录
                    itemLotIssued.setId(UUIDUtils.get32UUID())
                            .setStatus(StatusEnum.NORMAL.getCode())
                            .setLockStatus(YoNEnum.Y.getCode())
                            .setLockType(LotIssuedLockTypeEnum.MANUAL.getCode())
                            .setIssuedQty(itemLotIssued.getPostQty().longValue())
                            .setUnissuedQty(BigDecimal.ZERO)
                            .setAssignQty(itemLotIssued.getAssignQty());
                    insertList.add(itemLotIssued);
                    continue;
                }
                if (Objects.nonNull(itemLotIssued.getAssignQty())) {
                    // 分配数量不为空时，增加数量
                    dbLotIssued.setAssignQty(dbLotIssued.getAssignQty().add(itemLotIssued.getAssignQty()))
                            .setUnissuedQty(dbLotIssued.getUnissuedQty().add(itemLotIssued.getAssignQty()));
                }
                // 更新已领料和未领料数量
                BigDecimal issuedQty = this.calculateIssuedQty(itemLotIssued, dbLotIssued);
                dbLotIssued.setIssuedQty(issuedQty.longValue());
                BigDecimal unissuedQty = this.calculateUnissuedQty(dbLotIssued, dbLotIssued);
                dbLotIssued.setUnissuedQty(unissuedQty);
                updateList.add(dbLotIssued);
            }
        }

        for (WipReqLotIssuedEntity updateEntity : updateList) {
            EntityUtils.writeStdUpdInfoToEntity(updateEntity, EntityUtils.getWipUserId());
        }
        wipReqLotIssuedRepository.updateList(updateList);
        if (ListUtil.notEmpty(insertList)) {
            wipReqLotIssuedRepository.insertList(insertList);
        }
    }

    public void delete(WipReqLotIssuedEntity reqLotIssued) {
        reqLotIssued.checkItemKey();
        reqLotIssued.setStatus(StatusEnum.NORMAL.getCode());
        List<WipReqLotIssuedEntity> deleteEntityList = wipReqLotIssuedRepository.selectList(reqLotIssued);
        if (ListUtil.notEmpty(deleteEntityList)) {
            for (WipReqLotIssuedEntity deleteEntity : deleteEntityList) {
                this.checkDeletable(deleteEntity);
                deleteEntity.setStatus(StatusEnum.CLOSE.getCode());
                EntityUtils.writeStdUpdInfoToEntity(deleteEntity, EntityUtils.getWipUserId());
            }
            wipReqLotIssuedRepository.updateList(deleteEntityList);
        }
    }

    private List<WipReqLotIssuedEntity> getByItemKey(String organizationId, String headerId, String itemNo, String wkpNo) {
        WipReqLotIssuedEntity queryEntity = new WipReqLotIssuedEntity().setOrganizationId(organizationId).setHeaderId(headerId).setItemNo(itemNo).setWkpNo(wkpNo).setStatus(StatusEnum.NORMAL.getCode());
        return wipReqLotIssuedRepository.selectList(queryEntity);
    }

    private void classifyChanged(List<WipReqLotIssuedEntity> itemLotIssuedList, List<WipReqLotIssuedEntity> dbLotIssuedList,
                                 List<WipReqLotIssuedEntity> insertList, List<WipReqLotIssuedEntity> updateList, List<WipReqLotIssuedEntity> deleteList) {
        Set<String> updateLotSet = itemLotIssuedList.stream().map(WipReqLotIssuedEntity::getMtrLotNo).collect(Collectors.toSet());
        Set<String> deleteLotSet = dbLotIssuedList.stream().map(WipReqLotIssuedEntity::getMtrLotNo).collect(Collectors.toSet());
        Set<String> insertLotSet = new HashSet<>(updateLotSet);
        insertLotSet.removeIf(deleteLotSet::contains);
        updateLotSet.retainAll(deleteLotSet);
        deleteLotSet.removeIf(updateLotSet::contains);
        insertList.addAll(itemLotIssuedList.stream().filter(lot -> insertLotSet.contains(lot.getMtrLotNo())).collect(Collectors.toList()));
        // 新增的批次, 初始化已发料/未发料数量/锁定状态
        for (WipReqLotIssuedEntity insertEntity : insertList) {
            insertEntity.setIssuedQty(0L)
                    .setUnissuedQty(insertEntity.getAssignQty())
                    .setLockType(YoNEnum.Y.getCode().equals(insertEntity.getLockStatus()) ? LotIssuedLockTypeEnum.MANUAL.getCode() : LotIssuedLockTypeEnum.NONE.getCode());
        }
        deleteList.addAll(dbLotIssuedList.stream().filter(lot -> deleteLotSet.contains(lot.getMtrLotNo())).collect(Collectors.toList()));
        if (!updateLotSet.isEmpty()) {
            Map<String, WipReqLotIssuedEntity> tmpItemLotIssuedMap = WipReqLotIssuedEntity.toLotMap(itemLotIssuedList);
            Map<String, WipReqLotIssuedEntity> tmpDbLotIssuedMap = WipReqLotIssuedEntity.toLotMap(dbLotIssuedList);
            for (String updateLot : updateLotSet) {
                // 更新领料批次
                WipReqLotIssuedEntity itemLotIssued = tmpItemLotIssuedMap.get(updateLot);
                WipReqLotIssuedEntity dbLotIssued = tmpDbLotIssuedMap.get(updateLot);
                dbLotIssued.setAssignQty(itemLotIssued.getAssignQty())
                        .setUnissuedQty(this.calculateUnissuedQty(itemLotIssued, dbLotIssued))
                        .setLockStatus(itemLotIssued.getLockStatus());
                if (!LotIssuedLockTypeEnum.MANUAL.getCode().equals(dbLotIssued.getLockType()) && !itemLotIssued.getLockStatus().equals(dbLotIssued.getLockStatus())) {
                    // 批次锁定状态变更视为手动变更
                    dbLotIssued.setLockType(LotIssuedLockTypeEnum.MANUAL.getCode());
                }
                updateList.add(dbLotIssued);
            }
        }
    }

    private BigDecimal calculateUnissuedQty(WipReqLotIssuedEntity itemLotIssued, WipReqLotIssuedEntity dbLotIssued) {
        if (itemLotIssued.getAssignQty().longValue() < dbLotIssued.getIssuedQty()) {
            throw new ParamsIncorrectException(String.format("批次%s的分配数量不能小于已领数量", itemLotIssued.getMtrLotNo()));
        }
        // 未领料数量 = 分配数量 - 已领料数量
        return itemLotIssued.getAssignQty().subtract(BigDecimal.valueOf(dbLotIssued.getIssuedQty()));
    }

    private BigDecimal calculateIssuedQty(WipReqLotIssuedEntity itemLotIssued, WipReqLotIssuedEntity dbLotIssued) {
        if (Objects.isNull(itemLotIssued.getPostQty())) {
            throw new ParamsIncorrectException("过账数量不可为空");
        }
        // 已领料数量 += 过账数量
        Long issuedQty = Optional.ofNullable(dbLotIssued.getIssuedQty()).orElse(0L);
        return BigDecimal.valueOf(issuedQty).add(itemLotIssued.getPostQty());
    }

    private void checkDeletable(WipReqLotIssuedEntity deleteEntity) {
        if (deleteEntity.getIssuedQty() > 0) {
            throw new ParamsIncorrectException(String.format("批次%s已领料，不可删除", deleteEntity.getMtrLotNo()));
        }
    }

}
