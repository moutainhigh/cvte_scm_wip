package com.cvte.scm.wip.domain.core.patch.service.impl;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.scm.wip.domain.common.context.GlobalContext;
import com.cvte.scm.wip.domain.common.item.entity.MdItemEntity;
import com.cvte.scm.wip.domain.common.item.repository.MdItemRepository;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.util.CommonKVConvertAndValidator;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.util.CommonKVConvertAndValidatorFactory;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.validator.WipPatchLinesValidator;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.validator.WipPatchValidator;
import com.cvte.scm.wip.domain.common.patch.enums.PatchEnum;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchHistoryEntity;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchLinesEntity;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchWfEntity;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchHistoryRepository;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchLinesRepository;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchRepository;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchWfRepository;
import com.cvte.scm.wip.domain.core.patch.service.WipPatchLinesService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqMtrsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 4:31 下午
 */
@Service
public class WipPatchLinesServiceImpl implements WipPatchLinesService {

    private WipPatchLinesRepository wipPatchLinesRepository;

    private WipPatchWfRepository wipPatchWfRepository;

    private WipPatchRepository wipPatchRepository;

    private WipReqLineRepository wipReqLineRepository;

    private WipPatchHistoryRepository wipPatchHistoryRepository;

    private WipReqMtrsRepository wipReqMtrsRepository;

    private MdItemRepository mdItemRepository;

    public WipPatchLinesServiceImpl(WipPatchLinesRepository wipPatchLinesRepository, WipPatchWfRepository wipPatchWfRepository, WipPatchRepository wipPatchRepository, WipReqLineRepository wipReqLineRepository, WipPatchHistoryRepository wipPatchHistoryRepository, WipReqMtrsRepository wipReqMtrsRepository, MdItemRepository mdItemRepository) {
        this.wipPatchLinesRepository = wipPatchLinesRepository;
        this.wipPatchWfRepository = wipPatchWfRepository;
        this.wipPatchRepository = wipPatchRepository;
        this.wipReqLineRepository = wipReqLineRepository;
        this.wipPatchHistoryRepository = wipPatchHistoryRepository;
        this.wipReqMtrsRepository = wipReqMtrsRepository;
        this.mdItemRepository = mdItemRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveList(List<WipPatchLinesEntity> entityList,WipPatchEntity wipPatchEntity) {
        // 获取当前订单的状态
        wipPatchEntity = wipPatchRepository.selectById(wipPatchEntity.getBillId());
        WipPatchWfEntity wipPatchWfEntity = wipPatchWfRepository.selectOne(new WipPatchWfEntity().setId(wipPatchEntity.getWfId()));
        String status = wipPatchWfEntity.getStatus();
        // 1.状态历史
        // 获取用户id
        OperatingUser currentOperatingUser = GlobalContext.getCurrentOperatingUser();
        String userId = currentOperatingUser.getId();
        if (PatchEnum.CONFIRM.getCode().equals(status)){
            Date date = new Date();
            Integer wfId = wipPatchWfRepository.insertBatchReturnId(wipPatchWfEntity.setBillId(wipPatchEntity.getBillId()).setCrtDate(date).setCrtUser(userId).setStatus(PatchEnum.RECOMMIT.getCode()));
            wipPatchEntity.setWfId(wfId);
            wipPatchRepository.updateSelectiveById(wipPatchEntity);
        }
        Set<Serializable> set = new HashSet<>();
        // 校验行信息是否正常
        List<WipPatchLinesEntity> newList = new ArrayList<>();
        List<WipPatchLinesEntity> oldList = new ArrayList<>();
        entityList.forEach(v->{
            if (v.getId() == null){
                newList.add(v);
            }else {
                set.add(v.getId());
                oldList.add(v);
            }
        });
        // 校验行信息
        // 1.验老数据
        WipPatchLinesValidator wipPatchLinesValidator = null;
        CommonKVConvertAndValidator<String, WipPatchLinesEntity> linesValidator = CommonKVConvertAndValidatorFactory.buildByMapper(set, "id", WipPatchLinesRepository.class);
        wipPatchLinesValidator = new WipPatchLinesValidator(wipPatchEntity.getHeaderId(),status);
        linesValidator.addValidatorList(wipPatchLinesValidator);
        oldList.forEach(v->{
            if (!linesValidator.validatorAndConvert(v)) {
                throw new ParamsIncorrectException("参数出现异常，请检查");
            }
        });
        if (!newList.isEmpty()){
            insertList(newList,wipPatchEntity,status);
        }
        wipPatchLinesRepository.updateList(entityList);
        // 1.历史记录
        List<WipPatchHistoryEntity> historyList = wipPatchLinesValidator.getList();
        Date date = new Date();
        historyList.forEach(v->{
            v.setCrtDate(date).setCrtUser(userId);
            wipPatchHistoryRepository.insertSelective(v);
        });
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertList(List<WipPatchLinesEntity> entityList,WipPatchEntity wipPatchEntity, String status) {
        if (!PatchEnum.NEW.getCode().equals(status)){
            throw new ParamsIncorrectException("流程错误，请检查");
        }
        List<WipPatchLinesEntity> wipPatchLinesEntities = wipPatchLinesRepository.selectListByBillId(wipPatchEntity.getBillId(),null);
        Set<Serializable> itemIdVailtor = new HashSet<>();
        wipPatchLinesEntities.forEach(v->{
            itemIdVailtor.add(v.getId());
        });
        List<WipPatchHistoryEntity> historyList = new ArrayList<>();
        OperatingUser currentOperatingUser = GlobalContext.getCurrentOperatingUser();
        String userId = currentOperatingUser.getId();
        // 判断单据类型
        // 1.补料单
        if (WipPatchValidator.getPATCH().equals(wipPatchEntity.getBillType())){

            List<WipReqLineEntity> wipReqLineEntities = wipReqLineRepository.selectList(new WipReqLineEntity().setHeaderId(wipPatchEntity.getHeaderId()));
            Set<String> itemSet = new HashSet<>();
            wipReqLineEntities.forEach(v->{
                itemSet.add(v.getItemId());
            });
            entityList.forEach(v->{
                if (itemIdVailtor.contains(v.getItemId())){
                    throw new ParamsIncorrectException("数据重复，请检查");
                }
                if (!itemSet.contains(v.getItemId())){
                    if (wipReqMtrsRepository.countReqMtrs(wipPatchEntity.getHeaderId(), v.getItemId(), v.getItemId()) == 0){
                        throw new ParamsIncorrectException("参数出现异常，请检查");
                    }
                }
                historyList.add(new WipPatchHistoryEntity().setCrtDate(new Date()).setBillId(wipPatchEntity.getBillId()).setCrtUser(userId).setItemId(v.getItemId()).setItemCode(v.getItemCode()).setHistory("新建"));
                v.setBillId(wipPatchEntity.getBillId()).setIsShow("1");
                wipPatchLinesRepository.insertSelective(v);
            });
            // 2.通用补料单
        }else if (WipPatchValidator.getCOMMON().equals(wipPatchEntity.getBillType())){
            List<MdItemEntity> list = new ArrayList<>();
            entityList.forEach(v->{
                if (itemIdVailtor.contains(v.getItemId())){
                    throw new ParamsIncorrectException("数据重复，请检查");
                }
                list.add(new MdItemEntity().setInventoryItemId(v.getItemId()));
            });
            int itemList = mdItemRepository.getItemListCount(list);
            if (itemList != list.size()){
                throw new ParamsIncorrectException("参数出现异常，请检查");
            }
            entityList.forEach(v->{
                historyList.add(new WipPatchHistoryEntity().setCrtDate(new Date()).setBillId(wipPatchEntity.getBillId()).setCrtUser(userId).setItemId(v.getItemId()).setItemCode(v.getItemCode()).setHistory("新建"));
                v.setBillId(wipPatchEntity.getBillId()).setIsShow("1");
                wipPatchLinesRepository.insertSelective(v);
            });

        }
        // 记录历史记录
        historyList.forEach(v->{
            wipPatchHistoryRepository.insertSelective(v);
        });
    }

    @Override
    public List<WipPatchLinesEntity> selectList(String billId) {
        List<WipPatchLinesEntity> wipPatchLinesEntities = wipPatchLinesRepository.selectListByBillId(billId,null);
        return wipPatchLinesEntities;
    }

    @Override
    public void delete(String billId,Integer id) {
        WipPatchEntity wipPatchEntity = wipPatchRepository.selectById(billId);
        WipPatchWfEntity wipPatchWfEntity = wipPatchWfRepository.selectOne(new WipPatchWfEntity().setId(wipPatchEntity.getWfId()));
        if (!PatchEnum.NEW.getCode().equals(wipPatchWfEntity.getStatus())){
            throw new ParamsRequiredException("非法删除");
        }
        wipPatchLinesRepository.updateSelectiveById(new WipPatchLinesEntity().setId(id).setIsShow("0"));
    }

    @Override
    public boolean verifyWipPatchLines(String billId, String itemId) {
        if ( wipPatchLinesRepository.selectListByBillId(billId, itemId).isEmpty()){
            return true;
        }else {
            return false;
        }
    }

}
