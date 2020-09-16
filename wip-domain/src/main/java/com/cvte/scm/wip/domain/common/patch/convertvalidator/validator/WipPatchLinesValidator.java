package com.cvte.scm.wip.domain.common.patch.convertvalidator.validator;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.ValidatorAdapter;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.util.SpringContextUtils;
import com.cvte.scm.wip.domain.common.patch.enums.PatchEnum;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchHistoryEntity;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchLinesEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqMtrsRepository;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @descriptions: 投料单行信息自定义校验器规则
 * @author: ykccchen
 * @date: 2020/7/27 3:20 下午
 */

public class WipPatchLinesValidator implements ValidatorAdapter {

    private String headerId ;
    private String status ;
    @Getter
    private List<WipPatchHistoryEntity> list = new ArrayList<>();

    public WipPatchLinesValidator(String headerId,String status) {
        this.headerId = headerId;
        this.status = status;
    }

    @Override
    public boolean invoke(Object standardValue, Object target) {
        if (target instanceof WipPatchLinesEntity){
            WipPatchLinesEntity now =  (WipPatchLinesEntity)target;
            WipPatchLinesEntity old = (WipPatchLinesEntity)standardValue;
            WipPatchHistoryEntity wipPatchHistoryEntity = new WipPatchHistoryEntity();
            wipPatchHistoryEntity.setHistory("").setBillId(now.getBillId()).setItemId(old.getItemId()).setItemCode(old.getItemCode());
            if (!now.getItemId().equals(old.getItemId())){
                WipReqMtrsRepository bean = SpringContextUtils.getBean(WipReqMtrsRepository.class);
                if (bean == null){
                    throw new NullPointerException("WipReqMtrsRepository的实体不在IOC容器中");
                }
                int i = bean.countReqMtrs(headerId, old.getItemId(), now.getItemId());
                if (i == 0){
                    return false;
                }
                wipPatchHistoryEntity.setHistory(String.format("物料%s -> %s ; ",old.getItemCode(),now.getItemCode()));
            }

            if (now.getFinalQty() != null && !now.getFinalQty().equals(old.getFinalQty())){
                return false;
            }
            if (!PatchEnum.NEW.getCode().equals(status)){
                if (now.getReqQty().equals(old.getFinalQty())){

                }else if (now.getReqQty() < old.getFinalQty()){
                    wipPatchHistoryEntity.setHistory(wipPatchHistoryEntity.getHistory() + String.format("数量%s -> %s ; ",old.getReqQty(),now.getReqQty()));
                }else {
                    return false;
                }
            }
            if (StringUtils.isNotBlank(now.getReason())){
                if (!now.getReason().equals(old.getReason())){
                    wipPatchHistoryEntity.setHistory(wipPatchHistoryEntity.getHistory() + String.format("原因变更%s -> %s ; ",old.getReason(),now.getReason()));
                }
            }
            if (!wipPatchHistoryEntity.getHistory().equals("")){
                list.add(wipPatchHistoryEntity);
            }
            return true;
        }

        return false;
    }
}
