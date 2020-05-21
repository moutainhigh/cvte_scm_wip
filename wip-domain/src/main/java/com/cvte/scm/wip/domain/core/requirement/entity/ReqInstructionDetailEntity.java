package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInstructionDetailEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionDetailRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionDetailBuildVO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

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
public class ReqInstructionDetailEntity implements Entity<String> {

    @Resource
    private ReqInstructionDetailEntityFactory detailEntityFactory;

    private ReqInstructionDetailRepository detailRepository;

    public ReqInstructionDetailEntity(ReqInstructionDetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    @Override
    public String getUniqueId() {
        return instructionDetailId;
    }

    private String instructionDetailId;

    private String organizationId;

    private String sourceChangeDetailId;

    private String moLotNo;

    private String itemIdOld;

    private String itemIdNew;

    private String wkpNo;

    private String posNo;

    private BigDecimal itemQty;

    private String operationType;

    private String insStatus;

    private Date enableDate;

    private Date disableDate;

    public ReqInstructionDetailEntity createInstructionDetail(ReqInstructionDetailBuildVO detailBuildVO) {
        ReqInstructionDetailEntity detailEntity = detailEntityFactory.perfect(detailBuildVO);
        detailRepository.insert(detailEntity);
        return detailEntity;
    }

    public static ReqInstructionDetailEntity get() {
        return DomainFactory.get(ReqInstructionDetailEntity.class);
    }

}
