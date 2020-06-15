package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsDetailRepository;
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
public class ReqInsDetailRepositoryImpl implements ReqInsDetailRepository {

    private WipReqInsDMapper insDMapper;

    public ReqInsDetailRepositoryImpl(WipReqInsDMapper insDMapper) {
        this.insDMapper = insDMapper;
    }

    @Override
    public void insert(ReqInsDetailEntity entity) {
        WipReqInsDetailDO insDO = WipReqInsDetailDO.buildDO(entity);
        EntityUtils.writeStdCrtInfoToEntity(insDO, EntityUtils.getWipUserId());
        insDMapper.insertSelective(insDO);
    }

    @Override
    public void update(ReqInsDetailEntity entity) {
        WipReqInsDetailDO billDO = WipReqInsDetailDO.buildDO(entity);
        EntityUtils.writeStdUpdInfoToEntity(billDO, EntityUtils.getWipUserId());
        insDMapper.updateByPrimaryKeySelective(billDO);
    }

    @Override
    public List<ReqInsDetailEntity> getByInsId(String insId) {
        WipReqInsDetailDO queryDetail = new WipReqInsDetailDO();
        queryDetail.setInsHId(insId);
        List<WipReqInsDetailDO> billDetailDOList = insDMapper.select(queryDetail);
        if (ListUtil.empty(billDetailDOList)) {
            return null;
        }
        return WipReqInsDetailDO.batchBuildEntity(billDetailDOList);
    }

}
