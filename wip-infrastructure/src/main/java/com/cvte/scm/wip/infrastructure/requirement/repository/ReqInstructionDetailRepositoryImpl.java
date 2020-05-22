package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionDetailRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqInsDMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqInsDetailDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ReqInstructionDetailRepositoryImpl implements ReqInstructionDetailRepository {

    private WipReqInsDMapper insDMapper;

    public ReqInstructionDetailRepositoryImpl(WipReqInsDMapper insDMapper) {
        this.insDMapper = insDMapper;
    }

    @Override
    public void insert(ReqInstructionDetailEntity entity) {
        WipReqInsDetailDO billDO = WipReqInsDetailDO.buildDO(entity);
        EntityUtils.writeStdCrtInfoToEntity(billDO, EntityUtils.getWipUserId());
        insDMapper.insertSelective(billDO);
    }

    @Override
    public void update(ReqInstructionDetailEntity entity) {
        WipReqInsDetailDO billDO = WipReqInsDetailDO.buildDO(entity);
        EntityUtils.writeStdUpdInfoToEntity(billDO, EntityUtils.getWipUserId());
        insDMapper.insertSelective(billDO);
    }

    @Override
    public List<ReqInstructionDetailEntity> getByInsId(String insId) {
        WipReqInsDetailDO queryDetail = new WipReqInsDetailDO();
        queryDetail.setInsHId(insId);
        List<WipReqInsDetailDO> billDetailDOList = insDMapper.select(queryDetail);
        if (ListUtil.empty(billDetailDOList)) {
            return null;
        }
        return WipReqInsDetailDO.batchBuildEntity(billDetailDOList);
    }

}
