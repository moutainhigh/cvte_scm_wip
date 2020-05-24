package com.cvte.scm.wip.infrastructure.rework.repository;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.sys.base.entity.SysOrg;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.SysOrgDimensionEnum;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillHeaderEntity;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillHeaderRepository;
import com.cvte.scm.wip.domain.core.rework.valueobject.ApiReworkBillVO;
import com.cvte.scm.wip.domain.core.rework.valueobject.enums.WipMoReworkBillStatusEnum;
import com.cvte.scm.wip.infrastructure.rework.mapper.WipRwkBillHMapper;
import com.cvte.scm.wip.infrastructure.rework.mapper.dataobject.WipRwkBillHDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
  *
  * @author  : xueyuting
  * @since    : 2020/5/17 10:48
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReworkBillHeaderRepositoryImpl implements WipReworkBillHeaderRepository {

    private WipRwkBillHMapper billHMapper;

    public WipReworkBillHeaderRepositoryImpl(WipRwkBillHMapper billHMapper) {
        this.billHMapper = billHMapper;
    }

    @Override
    public WipReworkBillHeaderEntity selectByBillNo(String billNo) {
        WipRwkBillHDO queryBill = new WipRwkBillHDO().setBillNo(billNo);
        WipRwkBillHDO bill = billHMapper.selectOne(queryBill);
        return WipRwkBillHDO.buildEntity(bill);
    }

    @Override
    public List<WipReworkBillHeaderEntity> selectByBillNo(List<String> billNoList) {
        Example example = new Example(WipRwkBillHDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("billNo", billNoList);
        List<WipRwkBillHDO> billHDOList = billHMapper.selectByExample(example);

        List<WipReworkBillHeaderEntity> resultList = new ArrayList<>();
        for (WipRwkBillHDO billHDO : billHDOList) {
            WipReworkBillHeaderEntity billHeaderEntity = WipRwkBillHDO.buildEntity(billHDO);
            resultList.add(billHeaderEntity);
        }
        return resultList;
    }

    @Override
    public WipReworkBillHeaderEntity selectByBillId(String billId) {
        Example example = new Example(WipRwkBillHDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("billId", billId);
        criteria.andNotEqualTo("billStatus", WipMoReworkBillStatusEnum.INVALID.getCode());
        List<WipRwkBillHDO> queryResult = billHMapper.selectByExample(example);
        if (ListUtil.empty(queryResult)) {
            throw new ParamsIncorrectException(String.format("单据ID【%s】对应的单据不存在", billId));
        }

        WipReworkBillHeaderEntity billHeaderEntity = new WipReworkBillHeaderEntity();
        BeanUtils.copyProperties(queryResult.get(0), billHeaderEntity);
        return billHeaderEntity;
    }

    @Override
    public List<WipReworkBillHeaderEntity> selectBySourceOrderAndBillNo(String sourceOrderId, List<String> billNoList) {
        Example example = new Example(WipRwkBillHDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sourceOrderId", sourceOrderId);
        if (ListUtil.notEmpty(billNoList)) {
            criteria.andIn("billNo", billNoList);
        }
        List<WipRwkBillHDO> billHDOList = billHMapper.selectByExample(example);
        return WipRwkBillHDO.batchBuildEntity(billHDOList);
    }

    @Override
    public List<ApiReworkBillVO> selectByKeyList(List<String> keyList) {
        return billHMapper.selectByKeyList(keyList);
    }

    @Override
    public Map<String, String> selectOrgAbbrNameMap(List<String> organizationIdList) {
        Map<String, String> resultMap = new HashMap<>();
        for (String organizationId : organizationIdList) {
            // 公司ID对应名称, 可能会有多个, 减少查询次数
            SysOrg businessOrg = billHMapper.selectOrgByEbsCode(SysOrgDimensionEnum.GYL.getCode(), organizationId);
            resultMap.put(organizationId, Objects.nonNull(businessOrg) ? businessOrg.getAbbrName() : null);
        }
        return resultMap;
    }

    @Override
    public Map<String, String> selectFactoryNameMap(List<String> factoryIdList) {
        Map<String, String> resultMap = new HashMap<>();
        for (String factoryId : factoryIdList) {
            // 工厂ID对应名称, 也是为了减少查询次数
            SysOrg factory = billHMapper.selectOrgByEbsCode(SysOrgDimensionEnum.GC.getCode(), factoryId);
            resultMap.put(factoryId, Objects.nonNull(factory) ? factory.getOrgName() : null);
        }
        return resultMap;
    }

    @Override
    public void batchInsertSelective(List<WipReworkBillHeaderEntity> billHeaderList) {
        for (WipReworkBillHeaderEntity billHeaderEntity : billHeaderList) {
            WipRwkBillHDO billHDO = WipRwkBillHDO.buildDO(billHeaderEntity);
            billHMapper.insertSelective(billHDO);
        }
    }

    @Override
    public void update(WipReworkBillHeaderEntity billHeader) {
        WipRwkBillHDO billHDO = WipRwkBillHDO.buildDO(billHeader);
        billHMapper.updateByPrimaryKeySelective(billHDO);
    }

    @Override
    public void batchUpdate(List<WipReworkBillHeaderEntity> billHeaderList) {
        for (WipReworkBillHeaderEntity billHeaderEntity : billHeaderList) {
            this.update(billHeaderEntity);
        }
    }
}
