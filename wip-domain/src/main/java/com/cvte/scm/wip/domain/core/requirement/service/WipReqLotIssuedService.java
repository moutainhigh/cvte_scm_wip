package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.YoNEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedLockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public WipReqLotIssuedService(WipReqLotIssuedRepository wipReqLotIssuedRepository, CheckReqLineService checkReqLineService, CheckReqLotIssuedService checkReqLotIssuedService) {
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
        this.checkReqLineService = checkReqLineService;
        this.checkReqLotIssuedService = checkReqLotIssuedService;
    }

    public WipReqLotIssuedEntity selectById(String id) {
        return wipReqLotIssuedRepository.selectById(id);
    }

    public void save(WipReqLotIssuedEntity wipReqLotIssued) {
        checkReqLineService.checkItemExists(wipReqLotIssued.getOrganizationId(), wipReqLotIssued.getHeaderId(), wipReqLotIssued.getWkpNo(), wipReqLotIssued.getItemNo());
        checkReqLotIssuedService.verifyIssuedQty(wipReqLotIssued);
        if (StringUtils.isBlank(wipReqLotIssued.getId())) {
            // 新增
            wipReqLotIssued.setId(UUIDUtils.get32UUID())
                    .setStatus(StatusEnum.NORMAL.getCode());
            wipReqLotIssuedRepository.insert(wipReqLotIssued);
        } else {
            // 更新
            wipReqLotIssuedRepository.updateSelectiveById(wipReqLotIssued);
        }
    }

    public void invalid(String id) {
        WipReqLotIssuedEntity lotIssued = wipReqLotIssuedRepository.selectById(id);
        lotIssued.setStatus(StatusEnum.CLOSE.getCode());
        wipReqLotIssuedRepository.updateSelectiveById(lotIssued);
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
            deleteList.forEach(deleteEntity -> deleteEntity.setStatus(StatusEnum.CLOSE.getCode()));
            updateList.addAll(deleteList);
            for (WipReqLotIssuedEntity updateEntity : updateList) {
                // 更新信息
                EntityUtils.writeStdUpdInfoToEntity(updateEntity, EntityUtils.getWipUserId());
            }

            wipReqLotIssuedRepository.insertList(insertList);
            wipReqLotIssuedRepository.updateList(updateList);
        }
    }

    private List<WipReqLotIssuedEntity> getByItemKey(String organizationId, String headerId, String itemNo, String wkpNo) {
        WipReqLotIssuedEntity queryEntity = new WipReqLotIssuedEntity().setOrganizationId(organizationId).setHeaderId(headerId).setItemNo(itemNo).setWkpNo(wkpNo);
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
        deleteList.addAll(dbLotIssuedList.stream().filter(lot -> deleteLotSet.contains(lot.getMtrLotNo())).collect(Collectors.toList()));
        if (!updateLotSet.isEmpty()) {
            Map<String, WipReqLotIssuedEntity> tmpItemLotIssuedMap = WipReqLotIssuedEntity.toLotMap(itemLotIssuedList);
            Map<String, WipReqLotIssuedEntity> tmpDbLotIssuedMap = WipReqLotIssuedEntity.toLotMap(dbLotIssuedList);
            for (String updateLot : updateLotSet) {
                // 更新领料批次
                WipReqLotIssuedEntity itemLotIssued = tmpItemLotIssuedMap.get(updateLot);
                WipReqLotIssuedEntity dbLotIssued = tmpDbLotIssuedMap.get(updateLot);
                dbLotIssued.setIssuedQty(itemLotIssued.getIssuedQty());
                updateList.add(dbLotIssued);
            }
        }
    }

}
