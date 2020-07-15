package com.cvte.scm.wip.infrastructure.changebill.repository;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillDetailRepository;
import com.cvte.scm.wip.infrastructure.changebill.mapper.WipCnBillDMapper;
import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDetailDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 17:21
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ChangeBillDetailRepositoryImpl implements ChangeBillDetailRepository {

    private WipCnBillDMapper billDMapper;

    public ChangeBillDetailRepositoryImpl(WipCnBillDMapper billDMapper) {
        this.billDMapper = billDMapper;
    }

    @Override
    public void insert(ChangeBillDetailEntity entity) {
        WipCnBillDetailDO billDetailDO = WipCnBillDetailDO.buildDO(entity);
        EntityUtils.writeStdCrtInfoToEntity(billDetailDO, EntityUtils.getWipUserId());
        billDMapper.insertSelective(billDetailDO);
    }

    @Override
    public void update(ChangeBillDetailEntity entity) {
        WipCnBillDetailDO billDetailDO = WipCnBillDetailDO.buildDO(entity);
        EntityUtils.writeStdUpdInfoToEntity(billDetailDO, EntityUtils.getWipUserId());
        billDMapper.updateByPrimaryKeySelective(billDetailDO);
    }

    @Override
    public List<ChangeBillDetailEntity> selectByBillId(String billId) {
        Example example = new Example(WipCnBillDetailDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("billId", billId);
        criteria.andNotEqualTo("status", StatusEnum.CLOSE.getCode());
        List<WipCnBillDetailDO> billDetailDOList = billDMapper.selectByExample(example);
        if (ListUtil.empty(billDetailDOList)) {
            return null;
        }
        return WipCnBillDetailDO.batchBuildEntity(billDetailDOList);
    }

}
