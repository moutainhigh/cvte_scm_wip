package com.cvte.scm.wip.domain.core.changeorder.entity;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.changeorder.repository.ChangeOrderRepository;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderBuildVO;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderDetailBuildVO;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeReqVO;
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
public class ChangeOrderEntity implements Entity<String> {

    @Resource
    private DomainFactory<ChangeOrderBuildVO, ChangeOrderEntity> changeOrderEntityFactory;

    @Resource
    private DomainFactory<ChangeOrderBuildVO, UpdateChangeOrderEntity> updateChangeOrderEntityFactory;

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

    private List<ChangeOrderDetailEntity> orderDetailList;

    public List<ChangeOrderDetailEntity> getDetailById() {
        if (ListUtil.notEmpty(this.orderDetailList)) {
            return this.orderDetailList;
        }
        this.orderDetailList = ChangeOrderDetailEntity.get().getByBillId(this.billId);
        return this.orderDetailList;
    }

    public ChangeOrderEntity createChangeOrder(ChangeOrderBuildVO vo) {
        ChangeOrderEntity entity;
        if (StringUtils.isNotBlank(vo.getExistsBillId())) {
            entity = updateChangeOrderEntityFactory.perfect(vo);
        } else {
            switch (vo.getBillType()) {
                default:
                    entity = changeOrderEntityFactory.perfect(vo);
                    break;
            }
        }
        changeOrderRepository.insert(entity);
        return entity;
    }

    public ChangeOrderEntity creatCompleteChangeOrder(ChangeOrderBuildVO vo) {
        ChangeOrderEntity orderEntity = this.createChangeOrder(vo);

        ChangeOrderDetailEntity buildDetailEntity = ChangeOrderDetailEntity.get();
        List<ChangeOrderDetailEntity> orderDetailEntityList = new ArrayList<>();
        for (ChangeOrderDetailBuildVO detailBuildVO : vo.getDetailVOList()) {
            ChangeOrderDetailEntity orderDetailEntity = buildDetailEntity.createChangeOrderDetail(detailBuildVO);
            orderDetailEntityList.add(orderDetailEntity);
        }

        orderEntity.setOrderDetailList(orderDetailEntityList);
        return orderEntity;
    }

    public ReqInstructionBuildVO parseChangeOrder(ChangeReqVO reqHeaderVO) {
        // 生成投料单指令头
        ReqInstructionBuildVO instructionBuildVO = ReqInstructionBuildVO.buildVO(this);
        instructionBuildVO.setInstructionHeaderId(UUIDUtils.get32UUID())
                .setInstructionHeaderStatus("未确认")
                .setAimHeaderId(reqHeaderVO.getHeaderId())
                .setAimReqLotNo(reqHeaderVO.getSourceLotNo())
                .setExecuteType(ChangedTypeEnum.ADD);

        // 生成所有投料单指令行
        List<ReqInstructionDetailBuildVO> instructionDetailBuildVOList = new ArrayList<>();
        for (ChangeOrderDetailEntity orderDetailEntity : this.getOrderDetailList()) {
            ReqInstructionDetailBuildVO detailBuildVO = parseChangeOrderDetail(orderDetailEntity);
            instructionDetailBuildVOList.add(detailBuildVO);
        }

        instructionBuildVO.setDetailList(instructionDetailBuildVOList);
        return instructionBuildVO;
    }

    protected ReqInstructionDetailBuildVO addTypeDetailParse(ChangeOrderDetailEntity orderDetailEntity) {
        return defaultTypeDetailParse(orderDetailEntity);
    }

    protected ReqInstructionDetailBuildVO deleteTypeDetailParse(ChangeOrderDetailEntity orderDetailEntity) {
        return defaultTypeDetailParse(orderDetailEntity);
    }

    protected ReqInstructionDetailBuildVO replaceTypeDetailParse(ChangeOrderDetailEntity orderDetailEntity) {
        return defaultTypeDetailParse(orderDetailEntity);
    }

    protected ReqInstructionDetailBuildVO defaultTypeDetailParse(ChangeOrderDetailEntity orderDetailEntity) {
        ReqInstructionDetailBuildVO detailBuildVO = ReqInstructionDetailBuildVO.buildVO(orderDetailEntity);
        detailBuildVO.setInstructionDetailId(UUIDUtils.get32UUID())
                .setOperationType(orderDetailEntity.getOperationType())
                .setInsStatus(ReqInstructionStatusEnum.UNCONFIRMED.getCode());
        return detailBuildVO;
    }

    private ReqInstructionDetailBuildVO parseChangeOrderDetail(ChangeOrderDetailEntity orderDetailEntity) {
        String operationType = orderDetailEntity.getOperationType();
        ChangedTypeEnum changedTypeEnum = CodeableEnumUtils.getCodeableEnumByCode(operationType, ChangedTypeEnum.class);
        if (Objects.nonNull(changedTypeEnum)) {
            switch (changedTypeEnum) {
                case ADD:
                    return addTypeDetailParse(orderDetailEntity);
                case DELETE:
                    return deleteTypeDetailParse(orderDetailEntity);
                case REPLACE:
                    return replaceTypeDetailParse(orderDetailEntity);
            }
        }
        return defaultTypeDetailParse(orderDetailEntity);
    }

    /**
     * 最简单的得到自己新的实例
     * 复杂的请使用Factory
     */
    public static ChangeOrderEntity get() {
        return DomainFactory.get(ChangeOrderEntity.class);
    }

}
