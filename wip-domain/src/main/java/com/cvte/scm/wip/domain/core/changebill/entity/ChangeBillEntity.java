package com.cvte.scm.wip.domain.core.changebill.entity;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInstructionDetailBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ReqInstructionStatusEnum;
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
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/5/19 12:37
 */
@Slf4j
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChangeBillEntity implements Entity<String> {

    @Resource
    private DomainFactory<ChangeBillBuildVO, ChangeBillEntity> changeBillEntityFactory;

    @Resource
    private DomainFactory<ChangeBillBuildVO, UpdateChangeBillEntity> updateChangeBillEntityFactory;

    private ChangeBillRepository changeBillRepository;

    public ChangeBillEntity(ChangeBillRepository changeBillRepository) {
        this.changeBillRepository = changeBillRepository;
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

    private List<ChangeBillDetailEntity> billDetailList;

    public ChangeBillEntity getById(String billId) {
        return changeBillRepository.getById(billId);
    }

    public ChangeBillEntity getByNo(String billNo) {
        return changeBillRepository.getByNo(billNo);
    }

    public List<ChangeBillDetailEntity> getDetailById() {
        if (ListUtil.notEmpty(this.billDetailList)) {
            return this.billDetailList;
        }
        this.billDetailList = ChangeBillDetailEntity.get().getByBillId(this.billId);
        return this.billDetailList;
    }

    /**
     * 根据 更改单的类型/变更(撤销、更新) 生成对应的更改单实体
     * @since 2020/5/23 10:47 上午
     * @author xueyuting
     */
    public ChangeBillEntity createChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity entity;
        switch (vo.getBillType()) {
            default:
                entity = changeBillEntityFactory.perfect(vo);
                break;
        }
        changeBillRepository.insert(entity);
        return entity;
    }

    public ChangeBillEntity updateChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity entity = updateChangeBillEntityFactory.perfect(vo);
        changeBillRepository.update(entity);
        return entity;
    }

    public ChangeBillEntity saveChangeBill(ChangeBillBuildVO vo) {
        if (StringUtils.isNotBlank(vo.getBillId())) {
            return this.updateChangeBill(vo);
        }
        String billId = UUIDUtils.get32UUID();
        vo.setBillId(billId);
        vo.getDetailVOList().forEach(detail -> detail.setBillId(billId));
        return createChangeBill(vo);
    }

    /**
     * 生成更改单及其所有明细
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    public ChangeBillEntity completeChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity billEntity = this.saveChangeBill(vo);

        List<ChangeBillDetailEntity> billDetailEntityList = ChangeBillDetailEntity.get().batchSaveDetail(vo);

        billEntity.setBillDetailList(billDetailEntityList);
        return billEntity;
    }

    /**
     * 解析更改单
     * @since 2020/5/23 10:43 上午
     * @author xueyuting
     */
    public ReqInstructionBuildVO parseChangeBill(ChangeReqVO reqHeaderVO) {
        // 生成投料单指令头
        String instructionHeaderId = UUIDUtils.get32UUID();
        ReqInstructionBuildVO instructionBuildVO = ReqInstructionBuildVO.buildVO(this);
        instructionBuildVO.setInstructionHeaderId(instructionHeaderId)
                .setInstructionHeaderStatus("未确认")
                .setAimHeaderId(reqHeaderVO.getHeaderId())
                .setAimReqLotNo(reqHeaderVO.getSourceLotNo());

        // 生成所有投料单指令行
        List<ReqInstructionDetailBuildVO> instructionDetailBuildVOList = new ArrayList<>();
        for (ChangeBillDetailEntity billDetailEntity : this.getBillDetailList()) {
            // 解析更改单明细
            ReqInstructionDetailBuildVO detailBuildVO = parseChangeBillDetail(billDetailEntity);

            detailBuildVO.setInstructionHeaderId(instructionHeaderId);
            instructionDetailBuildVOList.add(detailBuildVO);
        }

        instructionBuildVO.setDetailList(instructionDetailBuildVOList);
        return instructionBuildVO;
    }

    /**
     * 新增类型的更改单明细解析
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    protected ReqInstructionDetailBuildVO addTypeDetailParse(ChangeBillDetailEntity billDetailEntity) {
        return defaultTypeDetailParse(billDetailEntity);
    }

    /**
     * 删除类型的更改单明细解析
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    protected ReqInstructionDetailBuildVO deleteTypeDetailParse(ChangeBillDetailEntity billDetailEntity) {
        return defaultTypeDetailParse(billDetailEntity);
    }

    /**
     * 替换类型的更改单明细解析
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    protected ReqInstructionDetailBuildVO replaceTypeDetailParse(ChangeBillDetailEntity billDetailEntity) {
        return defaultTypeDetailParse(billDetailEntity);
    }

    /**
     * 默认的更改单明细解析
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    protected ReqInstructionDetailBuildVO defaultTypeDetailParse(ChangeBillDetailEntity billDetailEntity) {
        ReqInstructionDetailBuildVO detailBuildVO = ReqInstructionDetailBuildVO.buildVO(billDetailEntity);
        detailBuildVO.setInstructionDetailId(UUIDUtils.get32UUID())
                .setOperationType(billDetailEntity.getOperationType())
                .setInsStatus(ReqInstructionStatusEnum.UNCONFIRMED.getCode());
        return detailBuildVO;
    }

    /**
     * 根据 更改单明细的变更类型 解析投料单指令
     * @since 2020/5/23 10:46 上午
     * @author xueyuting
     */
    private ReqInstructionDetailBuildVO parseChangeBillDetail(ChangeBillDetailEntity billDetailEntity) {
        String operationType = billDetailEntity.getOperationType();
        ChangedTypeEnum changedTypeEnum = CodeableEnumUtils.getCodeableEnumByCode(operationType, ChangedTypeEnum.class);
        if (Objects.nonNull(changedTypeEnum)) {
            switch (changedTypeEnum) {
                case ADD:
                    return addTypeDetailParse(billDetailEntity);
                case DELETE:
                    return deleteTypeDetailParse(billDetailEntity);
                case REPLACE:
                    return replaceTypeDetailParse(billDetailEntity);
            }
        }
        return defaultTypeDetailParse(billDetailEntity);
    }

    /**
     * 最简单的得到自己新的实例
     * 复杂的请使用Factory
     */
    public static ChangeBillEntity get() {
        return DomainFactory.get(ChangeBillEntity.class);
    }

}
