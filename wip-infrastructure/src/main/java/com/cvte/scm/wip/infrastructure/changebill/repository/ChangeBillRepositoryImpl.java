package com.cvte.scm.wip.infrastructure.changebill.repository;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.infrastructure.changebill.mapper.WipCnBillDMapper;
import com.cvte.scm.wip.infrastructure.changebill.mapper.WipCnBillMapper;
import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDO;
import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDetailDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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
    private WipCnBillDMapper cnBillDMapper;

    public ChangeBillRepositoryImpl(WipCnBillMapper cnBillMapper, WipCnBillDMapper cnBillDMapper) {
        this.cnBillMapper = cnBillMapper;
        this.cnBillDMapper = cnBillDMapper;
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

    /**
     * 根据指令集ID获取其对应的更改单
     * @since 2020/6/18 5:21 下午
     * @author xueyuting
     * @param reqInsHeaderId 指令头ID
     */
    @Override
    public ChangeBillEntity getByReqInsHeaderId(String reqInsHeaderId) {
        WipCnBillDO billDO = cnBillMapper.selectByReqInsHeaderId(reqInsHeaderId);
        if (Objects.isNull(billDO)) {
            return null;
        }
        return WipCnBillDO.buildEntity(billDO);
    }

    /**
     * 获取同步失败的更改单
     * @since 2020/6/18 5:20 下午
     * @author xueyuting
     * @param errMsgList 用于查询的同步失败信息
     */
    @Override
    public List<ChangeBillEntity> getSyncFailedBills(List<String> errMsgList) {
        List<WipCnBillDO> billDOList = cnBillMapper.selectSyncFailedBills(errMsgList);
        List<WipCnBillDetailDO> billDetailDOList = cnBillDMapper.selectSyncFailedDetails(errMsgList);

        // 把更改单组合起来
        List<ChangeBillEntity> billList = WipCnBillDO.batchBuildEntity(billDOList);
        List<ChangeBillDetailEntity> billDetailList = WipCnBillDetailDO.batchBuildEntity(billDetailDOList);
        Map<String, ChangeBillEntity> billMap = new HashMap<>();
        billList.forEach(bill -> billMap.put(bill.getBillId(), bill));
        for (ChangeBillDetailEntity detail : billDetailList) {
            ChangeBillEntity bill = billMap.get(detail.getBillId());
            List<ChangeBillDetailEntity> subDetailList = bill.getBillDetailList();
            if (ListUtil.empty(subDetailList)) {
                subDetailList = new ArrayList<>();
                bill.setBillDetailList(subDetailList);
            }
            subDetailList.add(detail);
        }
        return billList;
    }

}
