package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.enums.ChangeBillRecycleEnum;
import com.cvte.scm.wip.domain.core.changebill.valueobject.enums.ChangeBillTypeEnum;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/1 16:18
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class GenerateChangeBillDomainService implements DomainService {

    private WipReqHeaderService wipReqHeaderService;
    private ScmItemService itemService;

    public GenerateChangeBillDomainService(WipReqHeaderService wipReqHeaderService, ScmItemService itemService) {
        this.wipReqHeaderService = wipReqHeaderService;
        this.itemService = itemService;
    }

    public ChangeBillBuildVO generate(List<WipReqLineEntity> lineList, ChangeReqVO changeReqVO) {
        String headerId = lineList.stream().map(WipReqLineEntity::getHeaderId).distinct().collect(Collectors.toList()).get(0);
        if (StringUtils.isBlank(headerId)) {
            throw new ParamsIncorrectException("参数错误,headerId为空");
        }
        String organizationId = lineList.stream().map(WipReqLineEntity::getOrganizationId).distinct().collect(Collectors.toList()).get(0);
        if (StringUtils.isBlank(organizationId)) {
            throw new ParamsIncorrectException("参数错误,组织为空");
        }
        String changeReason = lineList.stream().map(WipReqLineEntity::getChangeReason).distinct().collect(Collectors.toList()).get(0);
        if (StringUtils.isBlank(changeReason)) {
            throw new ParamsIncorrectException("参数错误,替换原因为空");
        }
        WipReqHeaderEntity reqHeader = wipReqHeaderService.getByHeaderId(headerId);
        if (null == reqHeader) {
            throw new ParamsIncorrectException("找不到投料单");
        }
        ChangeReqVO reqVO = ChangeReqVO.buildVO(reqHeader);
        BeanUtils.copyProperties(reqVO, changeReqVO);

        Date now = new Date();
        DateFormat df = new SimpleDateFormat("HHmmss");
        String billNo = reqHeader.getSourceLotNo() + "_" + df.format(now);

        ChangeBillBuildVO billBuildVO = new ChangeBillBuildVO();
        billBuildVO.setBillNo(billNo)
                .setOrganizationId(organizationId)
                .setBillType(ChangeBillTypeEnum.FACTORY_CHANGE.getCode())
                .setMoId(reqHeader.getSourceId())
                .setBillStatus(StatusEnum.NORMAL.getCode())
                .setEnableDate(now)
                .setMotLotNo(reqHeader.getSourceLotNo())
                .setSourceNo(billNo)
                .setChangeContent(changeReason)
                .setFactoryId(reqHeader.getFactoryId())
                .setStatusType(reqHeader.getStatusType());

        List<String> itemNoList = lineList.stream().map(WipReqLineEntity::getBeforeItemNo).distinct().collect(Collectors.toList());
        itemNoList.addAll(lineList.stream().map(WipReqLineEntity::getItemNo).distinct().collect(Collectors.toList()));
        itemNoList.removeIf(StringUtils::isBlank);
        Map<String, String> itemNoIdMap = itemService.getItemMap(organizationId, itemNoList);
        List<ChangeBillDetailBuildVO> billDetailBuildVOList = new ArrayList<>();
        for (WipReqLineEntity line : lineList) {
            ChangeBillDetailBuildVO billDetailBuildVO = new ChangeBillDetailBuildVO();
            billDetailBuildVO.setMoLotNo(line.getLotNumber())
                    .setOrganizationId(organizationId)
                    .setStatus(StatusEnum.NORMAL.getCode())
                    .setItemIdOld(itemNoIdMap.get(line.getBeforeItemNo()))
                    .setItemIdNew(itemNoIdMap.get(line.getItemNo()))
                    .setWkpNo(line.getWkpNo())
                    .setPosNo(line.getPosNo())
                    .setOperationType(InsOperationTypeEnum.REPLACE.getCode())
                    .setItemUnitQty(BigDecimal.valueOf(line.getUnitQty()))
                    .setItemQty(BigDecimal.valueOf(line.getReqQty()))
                    .setSourceLineId(line.getLineId())
                    .setEnableDate(now)
                    .setIssueFlag(ChangeBillRecycleEnum.DONT_RECYCLE.getCode());
            billDetailBuildVOList.add(billDetailBuildVO);
        }

        billBuildVO.setDetailVOList(billDetailBuildVOList);
        return billBuildVO;
    }

}
