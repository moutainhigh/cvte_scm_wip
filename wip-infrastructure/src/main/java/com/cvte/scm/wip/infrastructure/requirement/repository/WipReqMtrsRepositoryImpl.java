package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqMtrsEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqMtrsRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqMtrsMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqMtrsDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 16:55
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqMtrsRepositoryImpl implements WipReqMtrsRepository {

    private WipReqMtrsMapper wipReqMtrsMapper;

    public WipReqMtrsRepositoryImpl(WipReqMtrsMapper wipReqMtrsMapper) {
        this.wipReqMtrsMapper = wipReqMtrsMapper;
    }

    @Override
    public Integer selectCount(WipReqMtrsEntity mtrsEntity) {
        List<String> subTypeList = new ArrayList<>();
        subTypeList.add("主");
        subTypeList.add("替");
        Example example = new Example(WipReqMtrsDO.class);
        example.createCriteria()
                .andEqualTo("organizationId", mtrsEntity.getOrganizationId())
                .andEqualTo("headerId", mtrsEntity.getHeaderId())
                .andEqualTo("itemNo", mtrsEntity.getItemNo())
                .andEqualTo("wkpNo", mtrsEntity.getWkpNo())
                .andIn("subType", subTypeList);
        return wipReqMtrsMapper.selectCountByExample(example);
    }

    @Override
    public List<String> selectMtrsItemNo(String headerId, String itemNo) {
        return wipReqMtrsMapper.selectMtrsItemNo(headerId, itemNo);
    }

    @Override
    public List<String> selectSubRuleMtrsItemNo(String headerId, String itemId) {
        return wipReqMtrsMapper.selectSubRuleMtrsItemNo(headerId, itemId);
    }

    @Override
    public List<WipReqMtrsEntity> getAllMtrs(String headerId, String itemId) {
        List<WipReqMtrsDO> allMtrs = wipReqMtrsMapper.getAllMtrs(headerId, itemId);
        List<WipReqMtrsEntity> list = WipReqMtrsDO.buildEntityList(allMtrs);
        return list;
    }

    @Override
    public int countReqMtrs(String headerId, String oldItemId, String nowItemId) {
        int i = wipReqMtrsMapper.countReqMtrs(headerId, oldItemId, nowItemId);
        return i;
    }
}
