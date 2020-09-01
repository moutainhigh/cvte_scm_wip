package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotIssuedRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqLotIssuedMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLotIssuedDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 15:59
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqLotIssuedRepositoryImpl implements WipReqLotIssuedRepository {

    private WipReqLotIssuedMapper wipReqLotIssuedMapper;

    public WipReqLotIssuedRepositoryImpl(WipReqLotIssuedMapper wipReqLotIssuedMapper) {
        this.wipReqLotIssuedMapper = wipReqLotIssuedMapper;
    }

    @Override
    public List<WipReqLotIssuedEntity> selectList(WipReqLotIssuedEntity lotIssuedEntity) {
        WipReqLotIssuedDO queryDO = WipReqLotIssuedDO.buildDO(lotIssuedEntity);
        List<WipReqLotIssuedDO> lotIssuedList = wipReqLotIssuedMapper.select(queryDO);

        List<WipReqLotIssuedEntity> resultList = new ArrayList<>();
        for (WipReqLotIssuedDO lotIssuedDO : lotIssuedList) {
            WipReqLotIssuedEntity issuedEntity = WipReqLotIssuedDO.buildEntity(lotIssuedDO);
            resultList.add(issuedEntity);
        }
        return resultList;
    }

    @Override
    public WipReqLotIssuedEntity selectById(String id) {
        WipReqLotIssuedDO queryDO = new WipReqLotIssuedDO().setId(id);
        List<WipReqLotIssuedDO> issuedDOList = wipReqLotIssuedMapper.select(queryDO);
        if (ListUtil.empty(issuedDOList)) {
            throw new ParamsIncorrectException("领料数据不存在");
        }
        return WipReqLotIssuedDO.buildEntity(issuedDOList.get(0));
    }

    @Override
    public List<WipReqLotIssuedEntity> selectById(List<String> idList) {
        if (ListUtil.empty(idList)) {
            return Collections.emptyList();
        }
        Example example = new Example(WipReqLotIssuedDO.class);
        example.createCriteria().andIn("id", idList);
        List<WipReqLotIssuedDO> issuedDOList = wipReqLotIssuedMapper.selectByExample(example);
        if (ListUtil.empty(issuedDOList)) {
            return Collections.emptyList();
        }
        return WipReqLotIssuedDO.batchBuildEntity(issuedDOList);
    }

    @Override
    public void insert(WipReqLotIssuedEntity lotIssuedEntity) {
        WipReqLotIssuedDO insertDO = WipReqLotIssuedDO.buildDO(lotIssuedEntity);
        EntityUtils.writeStdCrtInfoToEntity(insertDO, EntityUtils.getWipUserId());
        wipReqLotIssuedMapper.insertSelective(insertDO);
    }

    @Override
    public void update(WipReqLotIssuedEntity lotIssuedEntity) {
        WipReqLotIssuedDO updateDO = WipReqLotIssuedDO.buildDO(lotIssuedEntity);
        EntityUtils.writeStdUpdInfoToEntity(updateDO, EntityUtils.getWipUserId());
        wipReqLotIssuedMapper.updateByPrimaryKeySelective(updateDO);
    }

    @Override
    public void invalidById(String id) {
        WipReqLotIssuedDO invalidDO = new WipReqLotIssuedDO();
        invalidDO.setId(id)
                .setStatus(StatusEnum.CLOSE.getCode());
        EntityUtils.writeStdUpdInfoToEntity(invalidDO, EntityUtils.getWipUserId());
        wipReqLotIssuedMapper.updateByPrimaryKeySelective(invalidDO);
    }

    @Override
    public void invalidById(List<String> idList) {
        Example example = new Example(WipReqLotIssuedDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", idList);
        WipReqLotIssuedDO invalidEntity = new WipReqLotIssuedDO().setStatus(StatusEnum.CLOSE.getCode());
        EntityUtils.writeStdUpdInfoToEntity(invalidEntity, CurrentContext.getCurrentOperatingUser().getId());
        wipReqLotIssuedMapper.updateByExampleSelective(invalidEntity, example);
    }
}
