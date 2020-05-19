package com.cvte.scm.wip.domain.core.changeorder.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.core.changeorder.repository.ChangeOrderRepository;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 12:37
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChangeOrderEntity implements Entity<String> {

    @Resource
    private DomainFactory<ChangeOrderBuildVO, ChangeOrderEntity> changeOrderEntityFactory;

    @Resource
    private DomainFactory<ChangeOrderBuildVO, POChangeOrderEntity> poChangeOrderEntityFactory;

    private ChangeOrderRepository changeOrderRepository;

    public ChangeOrderEntity(ChangeOrderRepository changeOrderRepository) {
        this.changeOrderRepository = changeOrderRepository;
    }

    @Override
    public String getUniqueId() {
        return billId;
    }

    private String billId;

    private String billNo;

    private String organizationId;

    private String billType;

    private String moId;

    private String billStatus;

    private Date enableDate;

    private Date disableDate;

    public ChangeOrderEntity createChangeOrder(ChangeOrderBuildVO vo) {
        ChangeOrderEntity entity = null;
        if ("PO".equals(vo.getBillType())) {
            entity = poChangeOrderEntityFactory.perfect(vo);
        }
        changeOrderRepository.insert(entity);
        return entity;
    }

    public ReqInstructionBuildVO parseChangeOrder(List<ChangeOrderDetailEntity> orderDetailEntityList) {
        return null;
    }

    /**
     * 最简单的得到自己新的实例
     * 复杂的请使用Factory
     */
    public static ChangeOrderEntity get() {
        return DomainFactory.get(ChangeOrderEntity.class);
    }

}
