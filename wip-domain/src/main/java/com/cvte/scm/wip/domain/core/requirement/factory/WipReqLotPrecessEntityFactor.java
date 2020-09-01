package com.cvte.scm.wip.domain.core.requirement.factory;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLotProcessVO;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 17:44
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class WipReqLotPrecessEntityFactor implements DomainFactory<WipReqLotProcessVO, WipReqLotProcessEntity> {

    private ScmItemService itemService;

    public WipReqLotPrecessEntityFactor(ScmItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public WipReqLotProcessEntity perfect(WipReqLotProcessVO vo) {
        if (StringUtils.isBlank(vo.getItemId()) && StringUtils.isBlank(vo.getItemNo())) {
            throw new ParamsIncorrectException("物料不可为空");
        }
        if (StringUtils.isBlank(vo.getItemNo())) {
            vo.setItemNo(itemService.getItemNo(vo.getItemId()));
        }

        WipReqLotProcessEntity processEntity = WipReqLotProcessEntity.get();
        processEntity.setWipEntityId(vo.getWipEntityId())
                .setOrganizationId(vo.getOrganizationId())
                .setItemId(vo.getItemId())
                .setItemNo(vo.getItemNo())
                .setWkpNo(vo.getWkpNo())
                .setMtrLotNo(vo.getMtrLotNo())
                .setIssuedQty(vo.getIssuedQty())
                .setSourceCode(vo.getSourceCode());
        return processEntity;
    }

}
