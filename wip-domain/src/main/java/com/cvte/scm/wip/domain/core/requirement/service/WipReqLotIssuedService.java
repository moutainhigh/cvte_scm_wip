package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.YoNEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLotProcessVO;
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

}
