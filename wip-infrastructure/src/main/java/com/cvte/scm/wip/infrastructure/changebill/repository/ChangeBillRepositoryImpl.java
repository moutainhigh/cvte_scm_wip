package com.cvte.scm.wip.infrastructure.changebill.repository;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.infrastructure.changebill.mapper.WipCnBillMapper;
import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDO;
import org.springframework.stereotype.Repository;

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
        cnBillMapper.insertSelective(billDO);
    }

    @Override
    public void update(ChangeBillEntity entity) {
        WipCnBillDO billDO = WipCnBillDO.buildDO(entity);
        EntityUtils.writeStdUpdInfoToEntity(billDO, EntityUtils.getWipUserId());
        cnBillMapper.updateByPrimaryKeySelective(billDO);
    }

    @Override
    public ChangeBillEntity getById(String billId) {
        WipCnBillDO billDO = cnBillMapper.selectByPrimaryKey(billId);
        if (Objects.isNull(billDO)) {
            return null;
        }
        return WipCnBillDO.buildEntity(billDO);
    }

    @Override
    public ChangeBillEntity getByNo(String billNo) {
        WipCnBillDO queryBill = new WipCnBillDO();
        queryBill.setBillNo(billNo);
        List<WipCnBillDO> billDOList = cnBillMapper.select(queryBill);
        if (ListUtil.notEmpty(billDOList)) {
            return WipCnBillDO.buildEntity(billDOList.get(0));
        }
        return null;
    }

}
