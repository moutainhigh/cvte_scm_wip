package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqLinesMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLineDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 16:29
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqLineRepositoryImpl implements WipReqLineRepository {

    private WipReqLinesMapper wipReqLinesMapper;

    public WipReqLineRepositoryImpl(WipReqLinesMapper wipReqLinesMapper) {
        this.wipReqLinesMapper = wipReqLinesMapper;
    }

    @Override
    public WipReqLineEntity selectById(String id) {
        WipReqLineDO lineDO = wipReqLinesMapper.selectByPrimaryKey(id);
        return WipReqLineDO.buildEntity(lineDO);
    }

    @Override
    public List<WipReqLineEntity> selectList(WipReqLineEntity queryEntity) {
        WipReqLineDO queryDO = WipReqLineDO.buildDO(queryEntity);
        List<WipReqLineDO> lineDOList = wipReqLinesMapper.select(queryDO);
        if (ListUtil.empty(lineDOList)) {
            throw new ParamsIncorrectException("投料行数据不存在");
        }

        List<WipReqLineEntity> lineEntityList = new ArrayList<>();
        for (WipReqLineDO lineDO : lineDOList) {
            WipReqLineEntity lineEntity = WipReqLineDO.buildEntity(lineDO);
            lineEntityList.add(lineEntity);
        }
        return lineEntityList;
    }

    @Override
    public List<WipReqLineEntity> selectByExample(Example example) {
        List<WipReqLineDO> lineDOList = wipReqLinesMapper.selectByExample(example);

        List<WipReqLineEntity> lineEntityList = new ArrayList<>();
        for (WipReqLineDO lineDO : lineDOList) {
            WipReqLineEntity lineEntity = WipReqLineDO.buildEntity(lineDO);
            lineEntityList.add(lineEntity);
        }
        return lineEntityList;
    }

    @Override
    public void insertSelective(WipReqLineEntity lineEntity) {
        WipReqLineDO lineDO = WipReqLineDO.buildDO(lineEntity);
        wipReqLinesMapper.insertSelective(lineDO);
    }

    @Override
    public void updateSelectiveById(WipReqLineEntity lineEntity) {
        WipReqLineDO lineDO = WipReqLineDO.buildDO(lineEntity);
        wipReqLinesMapper.updateByPrimaryKeySelective(lineDO);
    }

    @Override
    public void writeIncrementalData(List<String> wipEntityIdList, List<Integer> organizationIdList) {
        wipReqLinesMapper.writeIncrementalData(wipEntityIdList, organizationIdList);
    }

}
