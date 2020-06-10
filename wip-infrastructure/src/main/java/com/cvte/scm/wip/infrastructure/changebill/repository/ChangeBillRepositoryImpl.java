package com.cvte.scm.wip.infrastructure.changebill.repository;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.infrastructure.changebill.mapper.WipCnBillMapper;
import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 17:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ChangeBillRepositoryImpl implements ChangeBillRepository {

    private WipCnBillMapper cnBillMapper;

    public ChangeBillRepositoryImpl(WipCnBillMapper cnBillMapper) {
        this.cnBillMapper = cnBillMapper;
    }

    @Override
    public void insert(ChangeBillEntity entity) {
        WipCnBillDO billDO = WipCnBillDO.buildDO(entity);
        EntityUtils.writeStdCrtInfoToEntity(billDO, EntityUtils.getWipUserId());
        if (Objects.nonNull(entity.getLastUpdDate())) {
            billDO.setUpdTime(entity.getLastUpdDate());
        }
        cnBillMapper.insertSelective(billDO);
    }

    @Override
    public void update(ChangeBillEntity entity) {
        WipCnBillDO billDO = WipCnBillDO.buildDO(entity);
        EntityUtils.writeStdUpdInfoToEntity(billDO, EntityUtils.getWipUserId());
        if (Objects.nonNull(entity.getLastUpdDate())) {
            billDO.setUpdTime(entity.getLastUpdDate());
        }
        cnBillMapper.updateByPrimaryKeySelective(billDO);
    }

    @Override
    public ChangeBillEntity getByKey(String billKey) {
        Example example = new Example(WipCnBillDO.class);
        example.createCriteria().andEqualTo("billId", billKey);
        Example.Criteria noCriteria = example.createCriteria().andEqualTo("billNo", billKey);
        example.or(noCriteria);
        example.createCriteria().andNotEqualTo("billStatus", StatusEnum.CLOSE.getCode());
        List<WipCnBillDO> billDOList = cnBillMapper.selectByExample(example);
        if (ListUtil.notEmpty(billDOList)) {
            return null;
        }
        if (billDOList.size() > 1) {
            throw new ParamsIncorrectException("更改单不惟一");
        }
        return WipCnBillDO.buildEntity(billDOList.get(0));
    }

    @Override
    public List<ChangeBillEntity> getById(List<String> billIdList) {
        Example example = new Example(WipCnBillDO.class);
        example.createCriteria().andIn("billId", billIdList);
        example.createCriteria().andNotEqualTo("billStatus", StatusEnum.CLOSE.getCode());
        List<WipCnBillDO> billDOList = cnBillMapper.selectByExample(example);
        if (ListUtil.empty(billDOList)) {
            return Collections.emptyList();
        }
        return WipCnBillDO.batchBuildEntity(billDOList);
    }

}
