package com.cvte.scm.wip.domain.core.changebill.entity;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillDetailRepository;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO;
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
  * @since    : 2020/5/19 15:27
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChangeBillDetailEntity implements Entity<String>{

    @Resource
    private DomainFactory<ChangeBillDetailBuildVO, ChangeBillDetailEntity> changeBillDetailEntityFactory;

    private ChangeBillDetailRepository changeBillDetailRepository;

    public ChangeBillDetailEntity(ChangeBillDetailRepository changeBillDetailRepository) {
        this.changeBillDetailRepository = changeBillDetailRepository;
    }

    @Override
    public String getUniqueId() {
        return detailId;
    }

    private String detailId;

    private String billId;

    private String moLotNo;

    private String status;

    private String organizationId;

    private String wkpNo;

    private String itemIdOld;

    private String itemIdNew;

    private BigDecimal itemQty;

    private BigDecimal itemUnitQty;

    private String operationType;

    private String posNo;

    private Date enableDate;

    private Date disableDate;

    private String sourceLineId;

    private String issueFlag;

    public List<ChangeBillDetailEntity> getByBillId(String billId) {
        return changeBillDetailRepository.selectByBillId(billId);
    }

    public ChangeBillDetailEntity createDetail(ChangeBillDetailBuildVO vo) {
        ChangeBillDetailEntity entity = changeBillDetailEntityFactory.perfect(vo);
        entity.setDetailId(UUIDUtils.get32UUID());
        changeBillDetailRepository.insert(entity);
        return entity;
    }

    public ChangeBillDetailEntity updateDetail(ChangeBillDetailBuildVO vo) {
        ChangeBillDetailEntity entity = changeBillDetailEntityFactory.perfect(vo);
        changeBillDetailRepository.update(entity);
        return entity;
    }

    public void deleteDetail(ChangeBillDetailEntity entity) {
        entity.setStatus(StatusEnum.CLOSE.getCode());
        changeBillDetailRepository.update(entity);
    }

    public List<ChangeBillDetailEntity> batchCreateDetail(List<ChangeBillDetailBuildVO> voList) {
        List<ChangeBillDetailEntity> createList = new ArrayList<>();
        for (ChangeBillDetailBuildVO detailBuildVO : voList) {
            createList.add(this.createDetail(detailBuildVO));
        }
        return createList;
    }

    public List<ChangeBillDetailEntity> batchUpdateDetail(List<ChangeBillDetailBuildVO> voList) {
        List<ChangeBillDetailEntity> updateList = new ArrayList<>();
        for (ChangeBillDetailBuildVO detailBuildVO : voList) {
            updateList.add(this.updateDetail(detailBuildVO));
        }
        return updateList;
    }

    public void batchDeleteDetail(List<ChangeBillDetailEntity> entityList) {
        for (ChangeBillDetailEntity entity : entityList) {
            this.deleteDetail(entity);
        }
    }

    /**
     * 批量保存更改单明细
     * @since 2020/5/23 10:49 上午
     * @author xueyuting
     */
    public List<ChangeBillDetailEntity> batchSaveDetail(ChangeBillBuildVO vo, boolean needDelete) {
        List<ChangeBillDetailEntity> resultDetailEntityList = new ArrayList<>();
        // 查询数据库现有明细
        List<ChangeBillDetailEntity> dbDetailEntityList = getByBillId(vo.getBillId());
        List<ChangeBillDetailBuildVO> detailVoList = vo.getDetailVOList();
        // 原始修改项按位号拆分后, 其 来源行Id 相同, 需要加上 位号 作为对比查询时的唯一键
        List<String> detailVoKeyList = detailVoList.stream().map(detailVo -> detailVo.getSourceLineId() + detailVo.getPosNo()).collect(Collectors.toList());
        if (ListUtil.notEmpty(dbDetailEntityList)) {
            // 可更新列表
            List<ChangeBillDetailBuildVO> updateVoList = new ArrayList<>(detailVoList);
            Map<String, ChangeBillDetailEntity> detailEntityMap = toMapByChangeDetailKey(dbDetailEntityList);
            Iterator<ChangeBillDetailBuildVO> iterator = updateVoList.iterator();
            while (iterator.hasNext()) {
                ChangeBillDetailBuildVO updateVo = iterator.next();
                ChangeBillDetailEntity detailEntity = detailEntityMap.get(updateVo.getSourceLineId() + updateVo.getPosNo());
                if (Objects.nonNull(detailEntity)) {
                    // 将ID设置为数据库的才可更新
                    updateVo.setDetailId(detailEntity.getDetailId());
                } else {
                    // 数据库不存在的剔除后, 剩下的都是需要更新的
                    iterator.remove();
                }
            }
            if (ListUtil.notEmpty(updateVoList)) {
                resultDetailEntityList.addAll(this.batchUpdateDetail(updateVoList));
            }

            // 数据库存在但是vo列表不存在, 则删除
            dbDetailEntityList.removeIf(detailEntity -> detailVoKeyList.contains(detailEntity.getSourceLineId() + detailEntity.getPosNo()));
            if (ListUtil.notEmpty(dbDetailEntityList) && needDelete) {
                this.batchDeleteDetail(dbDetailEntityList);
                resultDetailEntityList.addAll(dbDetailEntityList);
            }
        }

        // 剩下的新增
        if (resultDetailEntityList.size() != detailVoList.size()) {
            List<String> savedSourceIdList = resultDetailEntityList.stream().map(ChangeBillDetailEntity::getSourceLineId).collect(Collectors.toList());
            detailVoList.removeIf(detailVo -> savedSourceIdList.contains(detailVo.getSourceLineId()));
            if (ListUtil.notEmpty(detailVoList)) {
                resultDetailEntityList.addAll(this.batchCreateDetail(detailVoList));
            }
        }

        return resultDetailEntityList;
    }

    private Map<String, ChangeBillDetailEntity> toMapByChangeDetailKey(List<ChangeBillDetailEntity> billDetailEntityList) {
        Map<String, ChangeBillDetailEntity> entityMap = new HashMap<>();
        billDetailEntityList.forEach(detailEntity -> entityMap.put(detailEntity.getSourceLineId() + detailEntity.getPosNo(), detailEntity));
        return entityMap;
    }

    public static ChangeBillDetailEntity get() {
        return DomainFactory.get(ChangeBillDetailEntity.class);
    }

}
