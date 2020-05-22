package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqInsHMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqInsHeaderDO;
import org.springframework.stereotype.Repository;

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
        insHMapper.insertSelective(insDO);

    }

    @Override
    public ReqInstructionEntity getById(String insId) {
        WipReqInsHeaderDO insDO = insHMapper.selectByPrimaryKey(insId);
        if (Objects.isNull(insDO)) {
            return null;
        }
        return WipReqInsHeaderDO.buildEntity(insDO);
    }

}
