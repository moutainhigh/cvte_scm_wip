package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInstructionDetailEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionDetailRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionDetailBuildVO;
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

    private String instructionHeaderId;

    private String organizationId;

    private String sourceChangeDetailId;

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

    public List<ReqInstructionDetailEntity> getByInstructionId(String insHeaderId) {
        return detailRepository.getByInsId(insHeaderId);
    }

    public ReqInstructionDetailEntity createInstructionDetail(ReqInstructionDetailBuildVO vo) {
        ReqInstructionDetailEntity detailEntity = detailEntityFactory.perfect(vo);
        detailRepository.insert(detailEntity);
        return detailEntity;
    }

    public ReqInstructionDetailEntity updateInstructionDetail(ReqInstructionDetailBuildVO vo) {
        ReqInstructionDetailEntity detailEntity = detailEntityFactory.perfect(vo);
        detailRepository.update(detailEntity);
        return detailEntity;
    }

    public List<ReqInstructionDetailEntity> batchSaveInstructionDetail(ReqInstructionBuildVO vo) {
        List<ReqInstructionDetailEntity> detailEntityList = this.getByInstructionId(vo.getInstructionHeaderId());
        List<String> detailIdList = new ArrayList<>();
        if (ListUtil.notEmpty(detailEntityList)) {
            detailIdList.addAll(detailEntityList.stream().map(ReqInstructionDetailEntity::getInstructionDetailId).collect(Collectors.toList()));
        }

        List<ReqInstructionDetailEntity> resultDetailList = new ArrayList<>();
        for (ReqInstructionDetailBuildVO detailBuildVO : vo.getDetailList()) {
            if (detailIdList.contains(detailBuildVO.getInstructionDetailId())) {
                resultDetailList.add(this.updateInstructionDetail(detailBuildVO));
            } else {
                resultDetailList.add(this.createInstructionDetail(detailBuildVO));
            }
        }
        return resultDetailList;
    }

    public static ReqInstructionDetailEntity get() {
        return DomainFactory.get(ReqInstructionDetailEntity.class);
    }

}
