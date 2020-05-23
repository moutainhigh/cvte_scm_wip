package com.cvte.scm.wip.domain.core.changebill.entity;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public List<ChangeBillDetailEntity> getByBillId(String billId) {
        return changeBillDetailRepository.selectByBillId(billId);
    }

    public ChangeBillDetailEntity createDetail(ChangeBillDetailBuildVO vo) {
        ChangeBillDetailEntity entity = changeBillDetailEntityFactory.perfect(vo);
        changeBillDetailRepository.insert(entity);
        return entity;
    }

    public ChangeBillDetailEntity updateDetail(ChangeBillDetailBuildVO vo) {
        ChangeBillDetailEntity entity = changeBillDetailEntityFactory.perfect(vo);
        changeBillDetailRepository.update(entity);
        return entity;
    }

    /**
     * 批量保存更改单明细
     * @since 2020/5/23 10:49 上午
     * @author xueyuting
     */
    public List<ChangeBillDetailEntity> batchSaveDetail(ChangeBillBuildVO vo) {
        // 查询数据库现有明细
        List<ChangeBillDetailEntity> dbDetailEntityList = getByBillId(vo.getBillId());
        List<String> detailIdList = new ArrayList<>();
        if (ListUtil.notEmpty(dbDetailEntityList)) {
            detailIdList.addAll(dbDetailEntityList.stream().map(ChangeBillDetailEntity::getDetailId).collect(Collectors.toList()));
        }

        List<ChangeBillDetailEntity> resultDetailEntityList = new ArrayList<>();
        for (ChangeBillDetailBuildVO detailBuildVO : vo.getDetailVOList()) {
            if (detailIdList.contains(detailBuildVO.getDetailId())) {
                // 有则更新
                resultDetailEntityList.add(this.updateDetail(detailBuildVO));
            } else {
                // 无则新增
                resultDetailEntityList.add(this.createDetail(detailBuildVO));
            }
        }
        return resultDetailEntityList;
    }

    public static ChangeBillDetailEntity get() {
        return DomainFactory.get(ChangeBillDetailEntity.class);
    }

}
