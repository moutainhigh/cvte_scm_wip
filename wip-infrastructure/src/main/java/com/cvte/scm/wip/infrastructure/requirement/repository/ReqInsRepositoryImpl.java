package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqInsHMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqInsHeaderDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:53
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ReqInsRepositoryImpl implements ReqInsRepository {

    private final static String STATUS = "status";

    private WipReqInsHMapper insHMapper;

    public ReqInsRepositoryImpl(WipReqInsHMapper insHMapper) {
        this.insHMapper = insHMapper;
    }

    @Override
    public void insert(ReqInsEntity entity) {
        WipReqInsHeaderDO insDO = WipReqInsHeaderDO.buildDO(entity);
        EntityUtils.writeStdCrtInfoToEntity(insDO, EntityUtils.getWipUserId());
        insHMapper.insertSelective(insDO);
    }

    @Override
    public void update(ReqInsEntity entity) {
        WipReqInsHeaderDO insDO = WipReqInsHeaderDO.buildDO(entity);
        EntityUtils.writeStdUpdInfoToEntity(insDO, EntityUtils.getWipUserId());
        insHMapper.updateByPrimaryKeySelective(insDO);

    }

    @Override
    public ReqInsEntity selectByKey(String insKey) {
        if (StringUtils.isBlank(insKey)) {
            throw new ParamsIncorrectException("查询具体指令时主键不可为空");
        }
        Example example = new Example(WipReqInsHeaderDO.class);
        Example.Criteria headerIdCriteria = example.createCriteria().andEqualTo("insHId", insKey);
        headerIdCriteria.andNotEqualTo(STATUS, StatusEnum.CLOSE.getCode());
        Example.Criteria sourceBillCriteria = example.createCriteria().andEqualTo("sourceCnBillId", insKey);
        sourceBillCriteria.andNotEqualTo(STATUS, StatusEnum.CLOSE.getCode());
        example.or(sourceBillCriteria);
        List<WipReqInsHeaderDO> insDOList = insHMapper.selectByExample(example);
        if (ListUtil.empty(insDOList)) {
            return null;
        }
        if (insDOList.size() > 1) {
            throw new ParamsIncorrectException("投料单指令集不唯一");
        }
        return WipReqInsHeaderDO.buildEntity(insDOList.get(0));
    }

    @Override
    public List<ReqInsEntity> selectByAimHeaderId(String aimHeaderId, List<String> statusList) {
        Example example = new Example(WipReqInsHeaderDO.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("aimHeaderId", aimHeaderId);
        criteria.andIn(STATUS, statusList);
        example.orderBy("enableDate").asc();
        List<WipReqInsHeaderDO> headerDOList = insHMapper.selectByExample(example);
        return WipReqInsHeaderDO.batchBuildEntity(headerDOList);
    }

    @Override
    public List<String> getPreparedById(List<String> idList) {
        return insHMapper.selectPreparedById(idList);
    }

}
