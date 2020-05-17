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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
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
    public void insert(WipReqLotIssuedEntity lotIssuedEntity) {
        WipReqLotIssuedDO insertDO = WipReqLotIssuedDO.buildDO(lotIssuedEntity);
        wipReqLotIssuedMapper.insertSelective(insertDO);
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
