package com.cvte.scm.wip.domain.common.patch.convertvalidator.validator;

import com.cvte.scm.wip.domain.common.patch.convertvalidator.ValidatorAdapter;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;
import lombok.Getter;

/**
 * @version 1.0
 * @descriptions: 补料单数据校验
 * @author: ykccchen
 * @date: 2020/7/27 11:01 上午
 */
public class WipPatchValidator implements ValidatorAdapter {

    @Getter
    private static final String PATCH = "10";
    @Getter
    private static final String COMMON = "20";

    @Override
    public boolean invoke(Object standardValue, Object target) {
        if (target instanceof WipPatchEntity){
            WipPatchEntity now = (WipPatchEntity) target;
            WipPatchEntity old = (WipPatchEntity) standardValue;
            if (now.getWfId() != null && now.getWfId().equals(old.getWfId())){
                return false;
            }
            if (PATCH.equals(now.getBillType())) {
                if (now.getOrganizationId() != null && !now.getOrganizationId().equals(old.getOrganizationId())){
                    return false;
                }
                if (now.getFactoryId() != null && !now.getFactoryId().equals(old.getFactoryId())){
                    return false;
                }
                if (now.getMoId() != null && !now.getMoId().equals(old.getMoId())){
                    return false;
                }
                if (now.getMoLotNo() != null && !now.getMoLotNo().equals(old.getMoLotNo())){
                    return false;
                }
                if (now.getItemId() != null && !now.getItemId().equals(old.getItemId())){
                    return false;
                }
                return true;
            }else if (COMMON.equals(now.getBillType())){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }
}
