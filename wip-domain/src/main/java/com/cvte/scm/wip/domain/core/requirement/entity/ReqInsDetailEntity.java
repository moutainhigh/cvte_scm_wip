package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInsDetailEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsDetailRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsDetailBuildVO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:36
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqInsDetailEntity implements Entity<String> {

    @Resource
    private ReqInsDetailEntityFactory detailEntityFactory;

    private ReqInsDetailRepository detailRepository;

    public ReqInsDetailEntity(ReqInsDetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    @Override
    public String getUniqueId() {
        return insDetailId;
    }

    private String insDetailId;

    private String insHeaderId;

    private String organizationId;

    private String sourceDetailId;

    private String moLotNo;

    private String itemIdOld;

    private String itemIdNew;

    private String wkpNo;

    private String posNo;

    private BigDecimal itemQty;

    private BigDecimal itemUnitQty;

    private String operationType;

    private String insStatus;

    private Date enableDate;

    private Date disableDate;

    private Date confirmDate;

    private String confirmedBy;

    private String invalidBy;

    private String invalidReason;

    public List<ReqInsDetailEntity> getByInstructionId(String insHeaderId) {
        return detailRepository.getByInsId(insHeaderId);
    }

    public ReqInsDetailEntity createDetail(ReqInsDetailBuildVO vo) {
        ReqInsDetailEntity detailEntity = detailEntityFactory.perfect(vo);
        detailRepository.insert(detailEntity);
        return detailEntity;
    }

    public ReqInsDetailEntity updateDetail(ReqInsDetailBuildVO vo) {
        ReqInsDetailEntity detailEntity = detailEntityFactory.perfect(vo);
        detailRepository.update(detailEntity);
        return detailEntity;
    }

    public void deleteInsDetail(ReqInsDetailEntity entity) {
        entity.setInsStatus(StatusEnum.CLOSE.getCode());
        detailRepository.update(entity);
    }

    public List<ReqInsDetailEntity> batchCreateDetail(List<ReqInsDetailBuildVO> voList) {
        List<ReqInsDetailEntity> createList = new ArrayList<>();
        for (ReqInsDetailBuildVO detailBuildVO : voList) {
            createList.add(this.createDetail(detailBuildVO));
        }
        return createList;
    }

    public List<ReqInsDetailEntity> batchUpdateDetail(List<ReqInsDetailBuildVO> voList) {
        List<ReqInsDetailEntity> updateList = new ArrayList<>();
        for (ReqInsDetailBuildVO detailBuildVO : voList) {
            updateList.add(this.updateDetail(detailBuildVO));
        }
        return updateList;
    }

    public void batchDeleteDetail(List<ReqInsDetailEntity> entityList) {
        for (ReqInsDetailEntity entity : entityList) {
            entity.setInsStatus(StatusEnum.CLOSE.getCode());
            this.deleteInsDetail(entity);
        }
    }


    /**
     * 批量保存指令
     * @since 2020/5/23 10:41 上午
     * @author xueyuting
     */
    public List<ReqInsDetailEntity> batchSaveInsDetail(ReqInsBuildVO vo, Boolean needDelete) {
        List<ReqInsDetailEntity> resultDetailEntityList = new ArrayList<>();
        // 查询数据库现有明细
        List<ReqInsDetailEntity> dbDetailEntityList = getByInstructionId(vo.getInsHeaderId());
        List<ReqInsDetailBuildVO> detailVoList = vo.getDetailList();
        List<String> sourceDetailIdList = detailVoList.stream().map(ReqInsDetailBuildVO::getSourceChangeDetailId).collect(Collectors.toList());
        if (ListUtil.notEmpty(dbDetailEntityList)) {
            // 可更新列表
            List<ReqInsDetailBuildVO> updateVoList = new ArrayList<>(detailVoList);
            Map<String, ReqInsDetailEntity> detailEntityMap = toMapBySourceId(dbDetailEntityList);
            Iterator<ReqInsDetailBuildVO> iterator = updateVoList.iterator();
            while (iterator.hasNext()) {
                ReqInsDetailBuildVO updateVo = iterator.next();
                ReqInsDetailEntity detailEntity = detailEntityMap.get(updateVo.getSourceChangeDetailId());
                if (Objects.nonNull(detailEntity)) {
                    // 将ID设置为数据库的才可更新
                    updateVo.setInsDetailId(detailEntity.getInsDetailId());
                } else {
                    // 数据库不存在的剔除后, 剩下的都是需要更新的
                    iterator.remove();
                }
            }
            if (ListUtil.notEmpty(updateVoList)) {
                resultDetailEntityList.addAll(this.batchUpdateDetail(updateVoList));
            }

            // 数据库存在但是vo列表不存在, 则删除
            dbDetailEntityList.removeIf(detailEntity -> sourceDetailIdList.contains(detailEntity.getSourceDetailId()));
            if (ListUtil.notEmpty(dbDetailEntityList) && needDelete) {
                this.batchDeleteDetail(dbDetailEntityList);
                resultDetailEntityList.addAll(dbDetailEntityList);
            }
        }

        // 剩下的新增
        if (resultDetailEntityList.size() != detailVoList.size()) {
            List<String> savedSourceIdList = resultDetailEntityList.stream().map(ReqInsDetailEntity::getSourceDetailId).collect(Collectors.toList());
            detailVoList.removeIf(detailVo -> savedSourceIdList.contains(detailVo.getSourceChangeDetailId()));
            if (ListUtil.notEmpty(detailVoList)) {
                resultDetailEntityList.addAll(this.batchCreateDetail(detailVoList));
            }
        }

        return resultDetailEntityList;
    }

    private Map<String, ReqInsDetailEntity> toMapBySourceId(List<ReqInsDetailEntity> billDetailEntityList) {
        Map<String, ReqInsDetailEntity> entityMap = new HashMap<>();
        billDetailEntityList.forEach(detailEntity -> entityMap.put(detailEntity.getSourceDetailId(), detailEntity));
        return entityMap;
    }

    public static ReqInsDetailEntity get() {
        return DomainFactory.get(ReqInsDetailEntity.class);
    }

}
