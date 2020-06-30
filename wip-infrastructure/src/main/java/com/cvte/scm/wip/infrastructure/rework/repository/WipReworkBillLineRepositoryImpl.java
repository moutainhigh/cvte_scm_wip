package com.cvte.scm.wip.infrastructure.rework.repository;

import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillLineRepository;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkBillLVO;
import com.cvte.scm.wip.infrastructure.rework.mapper.WipRwkBillLMapper;
import com.cvte.scm.wip.infrastructure.rework.mapper.dataobject.WipRwkBillLDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/16 17:18
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReworkBillLineRepositoryImpl implements WipReworkBillLineRepository {

    private WipRwkBillLMapper wipRwkBillLMapper;

    public WipReworkBillLineRepositoryImpl(WipRwkBillLMapper wipRwkBillLMapper) {
        this.wipRwkBillLMapper = wipRwkBillLMapper;
    }

    @Override
    public List<WipRwkBillLVO> sumRwkQty(List<String> factoryIdList, List<String> moLotNoList) {
        return wipRwkBillLMapper.sumRwkQty(factoryIdList, moLotNoList);
    }

    @Override
    public List<WipReworkBillLineEntity> selectByBillId(String billId) {
        WipRwkBillLDO queryBillLine = new WipRwkBillLDO().setBillId(billId)
                .setStatus(StatusEnum.NORMAL.getCode());
        List<WipRwkBillLDO> billLDOList = wipRwkBillLMapper.select(queryBillLine);

        List<WipReworkBillLineEntity> resultList = new ArrayList<>();
        for (WipRwkBillLDO billLDO : billLDOList) {
            WipReworkBillLineEntity billLineEntity = WipRwkBillLDO.buildEntity(billLDO);
            resultList.add(billLineEntity);
        }
        return resultList;
    }

    @Override
    public List<WipReworkBillLineEntity> selectByBillId(List<String> billIdList) {
        Example billLineExample = new Example(WipRwkBillLDO.class);
        Example.Criteria billLineCriteria = billLineExample.createCriteria();
        billLineCriteria.andIn("billId", billIdList);
        billLineCriteria.andEqualTo("status", StatusEnum.NORMAL.getCode());
        List<WipRwkBillLDO> billLDOList = wipRwkBillLMapper.selectByExample(billLineExample);
        return WipRwkBillLDO.batchBuildEntity(billLDOList);
    }

    @Override
    public void batchInsert(List<WipReworkBillLineEntity> billLineList) {
        for (WipReworkBillLineEntity insertLine : billLineList) {
            WipRwkBillLDO billLDO = WipRwkBillLDO.buildDO(insertLine);
            wipRwkBillLMapper.insertSelective(billLDO);
        }
    }

    @Override
    public void batchUpdate(List<WipReworkBillLineEntity> billLineList) {
        for (WipReworkBillLineEntity insertLine : billLineList) {
            WipRwkBillLDO billLDO = WipRwkBillLDO.buildDO(insertLine);
            wipRwkBillLMapper.updateByPrimaryKeySelective(billLDO);
        }
    }

}
