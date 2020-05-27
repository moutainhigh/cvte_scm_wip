package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqInsHMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqInsHeaderDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:53
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ReqInstructionRepositoryImpl implements ReqInstructionRepository {

    private WipReqInsHMapper insHMapper;

    public ReqInstructionRepositoryImpl(WipReqInsHMapper insHMapper) {
        this.insHMapper = insHMapper;
    }

    @Override
    public void insert(ReqInstructionEntity entity) {
        WipReqInsHeaderDO insDO = WipReqInsHeaderDO.buildDO(entity);
        EntityUtils.writeStdCrtInfoToEntity(insDO, EntityUtils.getWipUserId());
        insHMapper.insertSelective(insDO);
    }

    @Override
    public void update(ReqInstructionEntity entity) {
        WipReqInsHeaderDO insDO = WipReqInsHeaderDO.buildDO(entity);
        EntityUtils.writeStdUpdInfoToEntity(insDO, EntityUtils.getWipUserId());
        insHMapper.updateByPrimaryKeySelective(insDO);

    }

    @Override
    public ReqInstructionEntity getByKey(String insKey) {
        Example example = new Example(WipReqInsHeaderDO.class);
        example.createCriteria().andEqualTo("insHId", insKey);
        Example.Criteria sourceBillCriteria = example.createCriteria().andEqualTo("sourceCnBillId", insKey);
        example.or(sourceBillCriteria);
        List<WipReqInsHeaderDO> insDOList = insHMapper.selectByExample(example);
        if (Objects.isNull(insDOList)) {
            return null;
        }
        if (insDOList.size() > 1) {
            throw new ParamsIncorrectException("投料单指令集不惟一");
        }
        return WipReqInsHeaderDO.buildEntity(insDOList.get(0));
    }

}
