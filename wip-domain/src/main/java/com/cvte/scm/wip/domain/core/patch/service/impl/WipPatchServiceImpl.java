package com.cvte.scm.wip.domain.core.patch.service.impl;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.commons.OperatingUserUnit;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.context.GlobalContext;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.util.CommonKVConvertAndValidator;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.util.CommonKVConvertAndValidatorFactory;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.validator.WipPatchValidator;
import com.cvte.scm.wip.domain.common.patch.enums.PatchEnum;
import com.cvte.scm.wip.domain.common.serial.SerialNoGenerationService;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchLinesEntity;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchWfEntity;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchLinesRepository;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchRepository;
import com.cvte.scm.wip.domain.core.patch.repository.WipPatchWfRepository;
import com.cvte.scm.wip.domain.core.patch.service.WipPatchLinesService;
import com.cvte.scm.wip.domain.core.patch.service.WipPatchService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.scm.repository.ScmViewCommonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 4:30 下午
 */
@Service
@Slf4j
public class WipPatchServiceImpl implements WipPatchService {


    private WipPatchRepository wipPatchRepository;

    private WipPatchLinesRepository wipPatchLinesRepository;

    private WipPatchWfRepository wipPatchWfRepository;

    private SerialNoGenerationService serialNoGenerationService;

    private WipPatchLinesService wipPatchLinesService;

    private WipReqHeaderRepository wipReqHeaderRepository;

    private ScmViewCommonRepository scmViewCommonRepository;

    public WipPatchServiceImpl(WipPatchRepository wipPatchRepository, WipPatchLinesRepository wipPatchLinesRepository, WipPatchWfRepository wipPatchWfRepository, SerialNoGenerationService serialNoGenerationService, WipPatchLinesService wipPatchLinesService, WipReqHeaderRepository wipReqHeaderRepository, ScmViewCommonRepository scmViewCommonRepository) {
        this.wipPatchRepository = wipPatchRepository;
        this.wipPatchLinesRepository = wipPatchLinesRepository;
        this.wipPatchWfRepository = wipPatchWfRepository;
        this.serialNoGenerationService = serialNoGenerationService;
        this.wipPatchLinesService = wipPatchLinesService;
        this.wipReqHeaderRepository = wipReqHeaderRepository;
        this.scmViewCommonRepository = scmViewCommonRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String insert(WipPatchEntity wipPatchEntity) {
        // 获取用户id
        OperatingUser currentOperatingUser = GlobalContext.getCurrentOperatingUser();
        String userId = currentOperatingUser.getId();
        // 获取组织
        OperatingUserUnit currentOperatingUserUnit = GlobalContext.getCurrentOperatingUserUnit();
        String currentUserOrgRelationId = currentOperatingUserUnit.getCurrentUserEbsCode();

        if (!WipPatchValidator.getCOMMON().equals(wipPatchEntity.getBillType())){
            //和工单表做校验
            WipReqHeaderEntity byHeader = wipReqHeaderRepository.getByHeaderId(wipPatchEntity.getHeaderId());
            if (byHeader == null){
                throw new ParamsIncorrectException("数据不匹配，请检查");
            }
            if (!byHeader.getFactoryId().equals(wipPatchEntity.getFactoryId())){
                throw new ParamsIncorrectException("数据不匹配，请检查");
            }else if(!byHeader.getSourceLotNo().equals(wipPatchEntity.getMoLotNo())){
                throw new ParamsIncorrectException("数据不匹配，请检查");
            }else if (!byHeader.getProductId().equals(String.valueOf(wipPatchEntity.getItemId()))){
                throw new ParamsIncorrectException("数据不匹配，请检查");
            }else if (!byHeader.getSourceId().equals(wipPatchEntity.getMoId())){
                throw new ParamsIncorrectException("数据不匹配，请检查");
            }
        }
        // 3.1获取流水线ID
        String factoryCodeById = "";
        if (StringUtils.isBlank(wipPatchEntity.getFactoryId())){
        }else {
             factoryCodeById = scmViewCommonRepository.getFactoryCodeById(wipPatchEntity.getFactoryId());
        }
        String billNo = serialNoGenerationService.getNextSerialNumberByCode("WIP_PATCH_ID");
        String billId = factoryCodeById + billNo;
        wipPatchEntity.setBillId(billId);
        // 开始插入数据
        // 1.状态历史
        try {
            WipPatchWfEntity wipPatchWfEntity = new WipPatchWfEntity();
            Date date = new Date();
            Integer wfId = wipPatchWfRepository.insertBatchReturnId(wipPatchWfEntity.setBillId(wipPatchEntity.getBillId()).setCrtDate(date).setCrtUser(userId).setStatus(PatchEnum.NEW.getCode()));
            wipPatchEntity.setWfId(wfId);
            // 2.更新行信息
            wipPatchLinesService.insertList(wipPatchEntity.getLinesList(),wipPatchEntity,PatchEnum.NEW.getCode());
            // 3.更新头信息
            wipPatchEntity.setOrganizationId(currentUserOrgRelationId);
            wipPatchRepository.insertSelective(wipPatchEntity);
        }catch (Exception e){
            log.error(e.toString());
        }
        return billId;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(WipPatchEntity wipPatchEntity,String status) {
        OperatingUser currentOperatingUser = GlobalContext.getCurrentOperatingUser();
        if (!PatchEnum.isValue(status)){
            throw new ParamsIncorrectException("错误流程，请检查");
        }
        // 设置校验器
        Set<Serializable> set = new HashSet<>();
        set.add(wipPatchEntity.getBillId());
        CommonKVConvertAndValidator<String, WipPatchEntity> billValidator = CommonKVConvertAndValidatorFactory.buildByMapper(set, "billId", WipPatchRepository.class);
        billValidator.addValidatorList(new WipPatchValidator());
        // 开始校验 校验不通过抛出异常
        if (!billValidator.validatorAndConvert(wipPatchEntity)) {
            throw new ParamsIncorrectException("参数出现异常，请检查");
        }
        // 开始更新数据
        // 1.状态历史
        try {
            WipPatchWfEntity wipPatchWfEntity = new WipPatchWfEntity();
            Date date = new Date();
            Integer wfId = wipPatchWfRepository.insertBatchReturnId(wipPatchWfEntity.setBillId(wipPatchEntity.getBillId()).setCrtDate(date).setCrtUser(currentOperatingUser.getId()).setStatus(status));
            wipPatchEntity.setWfId(wfId);
            // 2.更新头信息
            wipPatchRepository.updateSelectiveById(wipPatchEntity);
            // 3.确认流程和提交流程更新最终需求量
            if (PatchEnum.CONFIRM.getCode().equals(status) || PatchEnum.COMMIT.getCode().equals(status)){
                List<WipPatchLinesEntity> wipPatchLinesEntities = wipPatchLinesService.selectList(wipPatchEntity.getBillId());
                wipPatchLinesEntities.forEach(v->{
                    v.setFinalQty(v.getReqQty());
                });
                wipPatchLinesRepository.updateList(wipPatchLinesEntities);
            }
        }catch (Exception e){
            log.error(e.toString());
        }
        return 1;
    }
}
