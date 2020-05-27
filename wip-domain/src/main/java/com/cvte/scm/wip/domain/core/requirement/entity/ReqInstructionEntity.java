package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInstructionEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInstructionRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

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

    private List<ReqInstructionDetailEntity> detailList = Collections.emptyList();

    public ReqInstructionEntity getByKey(String insKey) {
        return headerRepository.getByKey(insKey);
    }

    public ReqInstructionEntity createInstruction(ReqInstructionBuildVO vo) {
        String newInstructionId = UUIDUtils.get32UUID();
        vo.setInstructionHeaderId(newInstructionId);
        vo.getDetailList().forEach(detail -> detail.setInstructionHeaderId(newInstructionId));
        ReqInstructionEntity instructionEntity = headerEntityFactory.perfect(vo);
        headerRepository.insert(instructionEntity);
        return instructionEntity;
    }

    public ReqInstructionEntity updateInstruction(ReqInstructionBuildVO vo) {
        ReqInstructionEntity instructionEntity = headerEntityFactory.perfect(vo);
        headerRepository.update(instructionEntity);
        return instructionEntity;
    }

    /**
     * 增量保存
     * @since 2020/5/23 10:41 上午
     * @author xueyuting
     */
    public ReqInstructionEntity saveInstruction(ReqInstructionBuildVO vo) {
        ReqInstructionEntity instructionEntity;
        if (StringUtils.isNotBlank(vo.getInstructionHeaderId())) {
            instructionEntity = this.updateInstruction(vo);
        } else {
            instructionEntity = this.createInstruction(vo);
        }
        return instructionEntity;
    }

    /**
     * 生成投料单指令集及其所有指令
     * @since 2020/5/23 10:40 上午
     * @author xueyuting
     */
    public ReqInstructionEntity completeInstruction(ReqInstructionBuildVO vo) {
        // 生成指令集
        ReqInstructionEntity instructionHeaderEntity = this.saveInstruction(vo);

        // 生成所有指令
        List<ReqInstructionDetailEntity> detailEntityList = ReqInstructionDetailEntity.get().batchSaveInstructionDetail(vo);

        instructionHeaderEntity.setDetailList(detailEntityList);
        return instructionHeaderEntity;
    }

    public static ReqInstructionEntity get() {
        return DomainFactory.get(ReqInstructionEntity.class);
    }

}
