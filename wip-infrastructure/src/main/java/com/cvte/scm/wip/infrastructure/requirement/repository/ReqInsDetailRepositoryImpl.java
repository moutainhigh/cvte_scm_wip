package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.item.repository.ScmItemRepository;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsDetailRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqInsDMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqInsDetailDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private ScmItemRepository scmItemRepository;

    public ReqInsDetailRepositoryImpl(WipReqInsDMapper insDMapper, ScmItemRepository scmItemRepository) {
        this.insDMapper = insDMapper;
        this.scmItemRepository = scmItemRepository;
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
        Example example = new Example(WipReqInsDetailDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("insHId", insId);
        criteria.andNotEqualTo("insStatus", StatusEnum.CLOSE.getCode());
        List<WipReqInsDetailDO> billDetailDOList = insDMapper.selectByExample(example);
        if (ListUtil.empty(billDetailDOList)) {
            return null;
        }

        // 获取物料编码
        Set<String> itemIdSet = new HashSet<>();
        for (WipReqInsDetailDO detail : billDetailDOList) {
            itemIdSet.add(detail.getItemIdOld());
            itemIdSet.add(detail.getItemIdNew());
        }
        itemIdSet.removeIf(StringUtils::isBlank);
        Map<String, String> itemNoMap = scmItemRepository.selectNoById(itemIdSet);

        List<ReqInsDetailEntity> insDetailEntityList = WipReqInsDetailDO.batchBuildEntity(billDetailDOList);
        for (ReqInsDetailEntity detailEntity : insDetailEntityList) {
            detailEntity.setItemNoOld(itemNoMap.get(detailEntity.getItemIdOld()));
            detailEntity.setItemNoNew(itemNoMap.get(detailEntity.getItemIdNew()));
        }
        return insDetailEntityList;
    }

}
