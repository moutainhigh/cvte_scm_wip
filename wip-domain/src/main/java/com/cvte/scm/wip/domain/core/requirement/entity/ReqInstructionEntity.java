package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInstructionEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionDetailBuildVO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:22
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqInstructionEntity implements Entity<String> {

    @Resource
    private ReqInstructionEntityFactory headerEntityFactory;

    private ReqInstructionRepository headerRepository;

    public ReqInstructionEntity(ReqInstructionRepository headerRepository) {
        this.headerRepository = headerRepository;
    }

    @Override
    public String getUniqueId() {
        return instructionHeaderId;
    }

    private String instructionHeaderId;

    private String sourceChangeBillId;

    private String status;

    private String aimHeaderId;

    private String aimReqLotNo;

    private Date enableDate;

    private Date disableDate;

    private String confirmedBy;

    private String invalidBy;

    private String invalidReason;

    private List<ReqInstructionDetailEntity> detailList;

    public ReqInstructionEntity getById(String insId) {
        return headerRepository.getById(insId);
    }

    public ReqInstructionEntity createInstruction(ReqInstructionBuildVO vo) {
        ReqInstructionEntity instructionEntity = headerEntityFactory.perfect(vo);
        headerRepository.insert(instructionEntity);
        return instructionEntity;
    }

    public ReqInstructionEntity updateInstruction(ReqInstructionBuildVO vo) {
        ReqInstructionEntity instructionEntity = headerEntityFactory.perfect(vo);
        headerRepository.update(instructionEntity);
        return instructionEntity;
    }

    public ReqInstructionEntity saveInstruction(ReqInstructionBuildVO vo) {
        ReqInstructionEntity instructionEntity = getById(vo.getInstructionHeaderId());
        if (Objects.nonNull(instructionEntity)) {
            instructionEntity = this.updateInstruction(vo);
        } else {
            instructionEntity = this.createInstruction(vo);
        }
        return instructionEntity;
    }

    public ReqInstructionEntity completeInstruction(ReqInstructionBuildVO vo) {
        ReqInstructionEntity instructionHeaderEntity = this.saveInstruction(vo);

        List<ReqInstructionDetailEntity> detailEntityList = ReqInstructionDetailEntity.get().batchSaveInstructionDetail(vo);

        instructionHeaderEntity.setDetailList(detailEntityList);
        return instructionHeaderEntity;
    }

    public static ReqInstructionEntity get() {
        return DomainFactory.get(ReqInstructionEntity.class);
    }

}
