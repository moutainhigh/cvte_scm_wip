package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/27 20:02
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
public class CheckReqLotIssuedService {

    private WipReqLotIssuedRepository wipReqLotIssuedRepository;
    private QueryReqLineService queryReqLineService;

    public CheckReqLotIssuedService(WipReqLotIssuedRepository wipReqLotIssuedRepository, QueryReqLineService queryReqLineService) {
        this.wipReqLotIssuedRepository = wipReqLotIssuedRepository;
        this.queryReqLineService = queryReqLineService;
    }

    /**
     * 校验领料批次数量
     *
     * @param wipReqLotIssued 领料数据
     * @author xueyuting
     * @since 2020/2/04 3:07 下午
     */
    public void verifyIssuedQty(WipReqLotIssuedEntity wipReqLotIssued) {
        // 已领料数据
        WipReqLotIssuedEntity queryLotEntity = new WipReqLotIssuedEntity().setHeaderId(wipReqLotIssued.getHeaderId())
                .setOrganizationId(wipReqLotIssued.getOrganizationId()).setItemNo(wipReqLotIssued.getItemNo()).setWkpNo(wipReqLotIssued.getWkpNo()).setStatus(StatusEnum.NORMAL.getCode());
        List<WipReqLotIssuedEntity> lotIssuedList = wipReqLotIssuedRepository.selectList(queryLotEntity);
        if (lotIssuedList == null) {
            lotIssuedList = new ArrayList<>();
        }

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
        long oldTotalLotIssuedQty = lotIssuedList.stream().mapToLong(WipReqLotIssuedEntity::getIssuedQty).sum();
        lotIssuedList.add(wipReqLotIssued);

        // 需求数据
        WipReqLineKeyQueryVO keyQueryVO = new WipReqLineKeyQueryVO();
        keyQueryVO.setHeaderId(wipReqLotIssued.getHeaderId())
                .setOrganizationId(wipReqLotIssued.getOrganizationId())
                .setItemNo(wipReqLotIssued.getItemNo())
                .setWkpNo(wipReqLotIssued.getWkpNo());
        List<WipReqLineEntity> reqLinesList = queryReqLineService.getValidLine(keyQueryVO);

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

}
