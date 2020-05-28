package com.cvte.scm.wip.domain.core.changebill.entity;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.toolkit.date.DateUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsDetailBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

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

    private Long version;

    private List<ChangeBillDetailEntity> billDetailList = Collections.emptyList();

    public ChangeBillEntity getByKey(String billKey) {
        ChangeBillEntity billEntity = changeBillRepository.getByKey(billKey);
        if (Objects.isNull(billEntity)) {
            return null;
        }
        ChangeBillBuildVO buildVO = new ChangeBillBuildVO();
        buildVO.setBillType(billEntity.getBillType());
        ChangeBillEntity rebuildBillEntity = changeBillEntityFactory.perfect(buildVO);
        BeanUtils.copyProperties(billEntity, rebuildBillEntity);
        return rebuildBillEntity;
    }

    public List<ChangeBillDetailEntity> getDetailById() {
        if (ListUtil.notEmpty(this.billDetailList)) {
            return this.billDetailList;
        }
        this.billDetailList = ChangeBillDetailEntity.get().getByBillId(this.billId);
        return this.billDetailList;
    }

    public ChangeBillEntity buildChangeBill(ChangeBillBuildVO vo) {
        return changeBillEntityFactory.perfect(vo);
    }

    /**
     * 根据 更改单的类型/变更(撤销、更新) 生成对应的更改单实体
     * @since 2020/5/23 10:47 上午
     * @author xueyuting
     */
    public ChangeBillEntity createChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity entity = changeBillEntityFactory.perfect(vo);
        entity.setBillId(UUIDUtils.get32UUID());
        changeBillRepository.insert(entity);
        return entity;
    }

    public ChangeBillEntity updateChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity entity = changeBillEntityFactory.perfect(vo);
        changeBillRepository.update(entity);
        return entity;
    }

    public ChangeBillEntity deleteChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity entity = changeBillEntityFactory.perfect(vo);
        entity.setBillStatus(StatusEnum.CLOSE.getCode());
        entity.setVersion(Long.valueOf(DateUtils.DateToString(new Date(), "yyyyMMddHHmmssSSS")));
        changeBillRepository.update(entity);
        return entity;
    }

    public ChangeBillEntity createCompleteChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity billEntity = this.createChangeBill(vo);
        vo.getDetailVOList().forEach(detail -> detail.setBillId(billEntity.getBillId()));
        List<ChangeBillDetailEntity> detailEntityList = ChangeBillDetailEntity.get().batchCreateDetail(vo.getDetailVOList());
        billEntity.setBillDetailList(detailEntityList);
        return billEntity;
    }

    public ChangeBillEntity updateCompleteChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity billEntity = this.updateChangeBill(vo);
        List<ChangeBillDetailEntity> detailEntityList = ChangeBillDetailEntity.get().batchSaveDetail(vo, true);
        billEntity.setBillDetailList(detailEntityList);
        return billEntity;
    }

    public ChangeBillEntity deleteCompleteChangeBill(ChangeBillBuildVO vo) {
        ChangeBillEntity billEntity = this.deleteChangeBill(vo);
        List<ChangeBillDetailEntity> billDetailEntityList = billEntity.getDetailById();
        ChangeBillDetailEntity.get().batchDeleteDetail(billDetailEntityList);
        return billEntity;
    }

    /**
     * 生成更改单及其所有明细
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    public ChangeBillEntity completeChangeBill(ChangeBillBuildVO vo) {
        if (StringUtils.isNotBlank(vo.getBillId())) {
            if (StatusEnum.CLOSE.getCode().equals(vo.getBillStatus())) {
                return this.deleteCompleteChangeBill(vo);
            } else {
                return this.updateCompleteChangeBill(vo);
            }
        } else {
            return this.createCompleteChangeBill(vo);
        }
    }

    /**
     * 解析更改单
     * @since 2020/5/23 10:43 上午
     * @author xueyuting
     */
    public ReqInsBuildVO parseChangeBill(ChangeReqVO reqHeaderVO) {
        ReqInsBuildVO instructionBuildVO = ReqInsBuildVO.buildVO(this);
        instructionBuildVO.setChangeType(ChangedTypeEnum.ADD.getCode());
        // 生成投料单指令头
        instructionBuildVO.setInsHeaderStatus(ProcessingStatusEnum.PENDING.getCode())
                .setAimHeaderId(reqHeaderVO.getHeaderId())
                .setAimReqLotNo(reqHeaderVO.getSourceLotNo());

        // 生成所有投料单指令行
        List<ReqInsDetailBuildVO> instructionDetailBuildVOList = new ArrayList<>();
        for (ChangeBillDetailEntity billDetailEntity : this.getBillDetailList()) {
            // 解析更改单明细
            ReqInsDetailBuildVO detailBuildVO = parseChangeBillDetail(billDetailEntity);
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
    protected ReqInsDetailBuildVO addTypeDetailParse(ChangeBillDetailEntity billDetailEntity) {
        return defaultTypeDetailParse(billDetailEntity);
    }

    /**
     * 删除类型的更改单明细解析
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    protected ReqInsDetailBuildVO deleteTypeDetailParse(ChangeBillDetailEntity billDetailEntity) {
        return defaultTypeDetailParse(billDetailEntity);
    }

    /**
     * 替换类型的更改单明细解析
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    protected ReqInsDetailBuildVO replaceTypeDetailParse(ChangeBillDetailEntity billDetailEntity) {
        return defaultTypeDetailParse(billDetailEntity);
    }

    /**
     * 默认的更改单明细解析
     * @since 2020/5/23 10:45 上午
     * @author xueyuting
     */
    protected ReqInsDetailBuildVO defaultTypeDetailParse(ChangeBillDetailEntity billDetailEntity) {
        ReqInsDetailBuildVO detailBuildVO = ReqInsDetailBuildVO.buildVO(billDetailEntity);
        detailBuildVO.setInsDetailId(UUIDUtils.get32UUID())
                .setOperationType(billDetailEntity.getOperationType())
                .setInsStatus(billDetailEntity.getStatus());
        return detailBuildVO;
    }

    /**
     * 根据 更改单明细的变更类型 解析投料单指令
     * @since 2020/5/23 10:46 上午
     * @author xueyuting
     */
    private ReqInsDetailBuildVO parseChangeBillDetail(ChangeBillDetailEntity billDetailEntity) {
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
