package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqInterfaceEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqInterfaceRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqInterfaceMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqInterfaceDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 21:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqInterfaceRepositoryImpl implements WipReqInterfaceRepository {

    private WipReqInterfaceMapper wipReqInterfaceMapper;

    public WipReqInterfaceRepositoryImpl(WipReqInterfaceMapper wipReqInterfaceMapper) {
        this.wipReqInterfaceMapper = wipReqInterfaceMapper;
    }

    @Override
    public List<WipReqInterfaceEntity> selectByGroupIds(String[] groupIds, String proceed) {
        List<WipReqInterfaceDO> interfaceDOList = wipReqInterfaceMapper.selectByGroupIds(groupIds, proceed);
        return WipReqInterfaceDO.batchBuildEntity(interfaceDOList);
    }

    @Override
    public List<WipReqInterfaceEntity> selectByInterfaceInIds(String[] interfaceIds, String proceed) {
        List<WipReqInterfaceDO> interfaceDOList = wipReqInterfaceMapper.selectByInterfaceInIds(interfaceIds, proceed);
        return WipReqInterfaceDO.batchBuildEntity(interfaceDOList);
    }

    @Override
    public List<WipReqInterfaceEntity> selectByNeedConfirm(String[] interfaceIds, String needConfirm) {
        List<WipReqInterfaceDO> interfaceDOList = wipReqInterfaceMapper.selectByNeedConfirm(interfaceIds, needConfirm);
        return WipReqInterfaceDO.batchBuildEntity(interfaceDOList);
    }

    @Override
    public int getNotSolvedNumber(String headerId, String needConfirm) {
        return wipReqInterfaceMapper.getNotSolvedNumber(headerId, needConfirm);
    }

    @Override
    public List<WipReqInterfaceEntity> selectOmissionData(String proceed) {
        List<WipReqInterfaceDO> interfaceDOList = wipReqInterfaceMapper.selectOmissionData(proceed);
        return WipReqInterfaceDO.batchBuildEntity(interfaceDOList);
    }

    @Override
    public List<WipReqInterfaceEntity> selectBetweenTimeInStatus(Date timeFrom, Date timeTo, String... proceed) {
        List<WipReqInterfaceDO> interfaceDOList = wipReqInterfaceMapper.selectBetweenTimeInStatus(timeFrom, timeTo, proceed);
        return WipReqInterfaceDO.batchBuildEntity(interfaceDOList);
    }

    @Override
    public void updateByIdSelective(WipReqInterfaceEntity interfaceEntity, List<String> idList) {
        Example example = new Example(WipReqInterfaceDO.class);
        example.createCriteria().andIn("interfaceInId", idList);
        WipReqInterfaceDO interfaceDO = WipReqInterfaceDO.buildDO(interfaceEntity);
        wipReqInterfaceMapper.updateByExampleSelective(interfaceDO, example);
    }

}
