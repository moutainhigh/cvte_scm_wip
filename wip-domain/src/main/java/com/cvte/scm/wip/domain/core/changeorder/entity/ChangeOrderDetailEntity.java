package com.cvte.scm.wip.domain.core.changeorder.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.core.changeorder.factory.ChangeOrderDetailEntityFactory;
import com.cvte.scm.wip.domain.core.changeorder.repository.ChangeOrderDetailRepository;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderDetailBuildVO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
public class ChangeOrderDetailEntity implements Entity<String>{

    @Resource
    private DomainFactory<ChangeOrderDetailBuildVO, ChangeOrderDetailEntity> changeOrderDetailEntityFactory;

    private ChangeOrderDetailRepository changeOrderDetailRepository;

    public ChangeOrderDetailEntity(ChangeOrderDetailRepository changeOrderDetailRepository) {
        this.changeOrderDetailRepository = changeOrderDetailRepository;
    }

    @Override
    public String getUniqueId() {
        return detailId;
    }

    private String detailId;

    private String billId;

    private String moLotNo;

    private String detailStatus;

    private String organizationId;

    private String wkpNo;

    private String itemIdOld;

    private String itemIdNew;

    private BigDecimal itemQty;

    private String operationType;

    private String posNo;

    private Date enableDate;

    private Date disableDate;

    public List<ChangeOrderDetailEntity> getByBillId(String billId) {
        return changeOrderDetailRepository.selectByBillId(billId);
    }

    public ChangeOrderDetailEntity createChangeOrderDetail(ChangeOrderDetailBuildVO vo) {
        ChangeOrderDetailEntity entity = changeOrderDetailEntityFactory.perfect(vo);
        changeOrderDetailRepository.insert(entity);
        return entity;
    }

    public static ChangeOrderDetailEntity get() {
        return DomainFactory.get(ChangeOrderDetailEntity.class);
    }

}
