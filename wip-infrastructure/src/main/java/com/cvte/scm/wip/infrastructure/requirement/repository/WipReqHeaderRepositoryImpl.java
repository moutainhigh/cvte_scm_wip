package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqHeaderMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqHeaderDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:04
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqHeaderRepositoryImpl implements WipReqHeaderRepository {

    private WipReqHeaderMapper  wipReqHeaderMapper;

    public WipReqHeaderRepositoryImpl(WipReqHeaderMapper wipReqHeaderMapper) {
        this.wipReqHeaderMapper = wipReqHeaderMapper;
    }

    @Override
    public String getSourceId(String headerId) {
        return wipReqHeaderMapper.getSourceId(headerId);
    }

    @Override
    public List<WipReqHeaderEntity> selectList(WipReqHeaderEntity headerEntity) {
        WipReqHeaderDO queryDO = WipReqHeaderDO.buildDO(headerEntity);
        List<WipReqHeaderDO> headerDOList = wipReqHeaderMapper.select(queryDO);
        return WipReqHeaderDO.batchBuildEntity(headerDOList);
    }

    @Override
    public List<WipReqHeaderEntity> selectByExample(Example example) {
        List<WipReqHeaderDO> headerDOList = wipReqHeaderMapper.selectByExample(example);
        return WipReqHeaderDO.batchBuildEntity(headerDOList);
    }

    @Override
    public List<WipReqHeaderEntity> selectAddedData(List<Integer> organizationIdList) {
        List<WipReqHeaderDO> headerDOList = wipReqHeaderMapper.selectAddedData(organizationIdList);
        return WipReqHeaderDO.batchBuildEntity(headerDOList);
    }

    @Override
    public void updateStatusById(String billStatus, String headerId) {
        WipReqHeaderDO header = new WipReqHeaderDO().setBillStatus(BillStatusEnum.PREPARED.getCode());
        EntityUtils.writeStdUpdInfoToEntity(header, EntityUtils.getWipUserId());
        Example example = new Example(WipReqHeaderDO.class);
        example.createCriteria().andEqualTo("headerId", headerId);
        wipReqHeaderMapper.updateByExampleSelective(header, example);
    }

}
