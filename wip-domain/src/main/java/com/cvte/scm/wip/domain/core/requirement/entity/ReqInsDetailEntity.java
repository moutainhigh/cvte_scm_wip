package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInsDetailEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsDetailRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipLotRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsDetailBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
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
public class ReqInsDetailEntity implements Entity<String> {

    @Resource
    private ReqInsDetailEntityFactory detailEntityFactory;

    private ReqInsDetailRepository detailRepository;
    private ScmItemService itemService;
    private WipLotRepository wipLotRepository;

    public ReqInsDetailEntity(ReqInsDetailRepository detailRepository, ScmItemService itemService, WipLotRepository wipLotRepository) {
        this.detailRepository = detailRepository;
        this.itemService = itemService;
        this.wipLotRepository = wipLotRepository;
    }

    @Override
    public String getUniqueId() {
        return insDetailId;
    }

    private String insDetailId;

    private String insHeaderId;

    private String organizationId;

    private String sourceDetailId;

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

    private Date confirmDate;

    private String confirmedBy;

    private String invalidBy;

    private String invalidReason;

    // 冗余目标投料单头ID
    private String aimHeaderId;

    public List<ReqInsDetailEntity> getByInstructionId(String insHeaderId) {
        return detailRepository.getByInsId(insHeaderId);
    }

    public ReqInsDetailEntity createDetail(ReqInsDetailBuildVO vo) {
        ReqInsDetailEntity detailEntity = detailEntityFactory.perfect(vo);
        detailRepository.insert(detailEntity);
        return detailEntity;
    }

    public ReqInsDetailEntity updateDetail(ReqInsDetailBuildVO vo) {
        ReqInsDetailEntity detailEntity = detailEntityFactory.perfect(vo);
        detailRepository.update(detailEntity);
        return detailEntity;
    }

    public void deleteInsDetail(ReqInsDetailEntity entity) {
        entity.setInsStatus(StatusEnum.CLOSE.getCode());
        detailRepository.update(entity);
    }

    public List<ReqInsDetailEntity> batchCreateDetail(List<ReqInsDetailBuildVO> voList) {
        List<ReqInsDetailEntity> createList = new ArrayList<>();
        for (ReqInsDetailBuildVO detailBuildVO : voList) {
            createList.add(this.createDetail(detailBuildVO));
        }
        return createList;
    }

    public List<ReqInsDetailEntity> batchUpdateDetail(List<ReqInsDetailBuildVO> voList) {
        List<ReqInsDetailEntity> updateList = new ArrayList<>();
        for (ReqInsDetailBuildVO detailBuildVO : voList) {
            updateList.add(this.updateDetail(detailBuildVO));
        }
        return updateList;
    }

    public void batchDeleteDetail(List<ReqInsDetailEntity> entityList) {
        for (ReqInsDetailEntity entity : entityList) {
            entity.setInsStatus(StatusEnum.CLOSE.getCode());
            this.deleteInsDetail(entity);
        }
    }


    /**
     * 批量保存指令
     * @since 2020/5/23 10:41 上午
     * @author xueyuting
     */
    public List<ReqInsDetailEntity> batchSaveInsDetail(ReqInsBuildVO vo, Boolean needDelete) {
        List<ReqInsDetailEntity> resultDetailEntityList = new ArrayList<>();
        // 查询数据库现有明细
        List<ReqInsDetailEntity> dbDetailEntityList = getByInstructionId(vo.getInsHeaderId());
        List<ReqInsDetailBuildVO> detailVoList = vo.getDetailList();
        List<String> sourceDetailIdList = detailVoList.stream().map(ReqInsDetailBuildVO::getSourceChangeDetailId).collect(Collectors.toList());
        if (ListUtil.notEmpty(dbDetailEntityList)) {
            // 可更新列表
            List<ReqInsDetailBuildVO> updateVoList = new ArrayList<>(detailVoList);
            Map<String, ReqInsDetailEntity> detailEntityMap = toMapBySourceId(dbDetailEntityList);
            Iterator<ReqInsDetailBuildVO> iterator = updateVoList.iterator();
            while (iterator.hasNext()) {
                ReqInsDetailBuildVO updateVo = iterator.next();
                ReqInsDetailEntity detailEntity = detailEntityMap.get(updateVo.getSourceChangeDetailId());
                if (Objects.nonNull(detailEntity)) {
                    // 将ID设置为数据库的才可更新
                    updateVo.setInsDetailId(detailEntity.getInsDetailId());
                } else {
                    // 数据库不存在的剔除后, 剩下的都是需要更新的
                    iterator.remove();
                }
            }
            if (ListUtil.notEmpty(updateVoList)) {
                resultDetailEntityList.addAll(this.batchUpdateDetail(updateVoList));
            }

            // 数据库存在但是vo列表不存在, 则删除
            dbDetailEntityList.removeIf(detailEntity -> sourceDetailIdList.contains(detailEntity.getSourceDetailId()));
            if (ListUtil.notEmpty(dbDetailEntityList) && needDelete) {
                this.batchDeleteDetail(dbDetailEntityList);
                resultDetailEntityList.addAll(dbDetailEntityList);
            }
        }

        // 剩下的新增
        if (resultDetailEntityList.size() != detailVoList.size()) {
            List<String> savedSourceIdList = resultDetailEntityList.stream().map(ReqInsDetailEntity::getSourceDetailId).collect(Collectors.toList());
            detailVoList.removeIf(detailVo -> savedSourceIdList.contains(detailVo.getSourceChangeDetailId()));
            if (ListUtil.notEmpty(detailVoList)) {
                resultDetailEntityList.addAll(this.batchCreateDetail(detailVoList));
            }
        }

        return resultDetailEntityList;
    }

    private Map<String, ReqInsDetailEntity> toMapBySourceId(List<ReqInsDetailEntity> billDetailEntityList) {
        Map<String, ReqInsDetailEntity> entityMap = new HashMap<>();
        billDetailEntityList.forEach(detailEntity -> entityMap.put(detailEntity.getSourceDetailId(), detailEntity));
        return entityMap;
    }

    public List<WipReqLineEntity> parseDetail(Map<String, List<WipReqLineEntity>> reqLineMap) {
        InsOperationTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(this.getOperationType(), InsOperationTypeEnum.class);
        if (Objects.isNull(typeEnum)) {
            throw new ParamsIncorrectException("投料指令变更类型异常");
        }
        switch (typeEnum) {
            case ADD:
                return parseAddType();
            case DELETE:
                return parseDeleteType(reqLineMap);
            case REPLACE:
                return parseReplaceType(reqLineMap);
        }
        return Collections.emptyList();
    }

    private WipReqLineEntity buildReqLine() {
        WipReqLineEntity reqLine = new WipReqLineEntity();
        reqLine.setHeaderId(this.getAimHeaderId())
                .setOrganizationId(this.getOrganizationId())
                .setWkpNo(this.getWkpNo())
                .setLotNumber(this.getMoLotNo())
                .setPosNo(this.getPosNo())
                .setItemId(this.getItemIdNew())
                .setItemNo(itemService.getItemNo(this.getOrganizationId(), this.getItemIdNew()))
                .setReqQty(this.getItemQty().intValue())
                .setUnitQty(this.getItemUnitQty().doubleValue())
                .setChangeType(ChangedTypeEnum.ADD.getCode());
        return reqLine;
    }

    private List<WipReqLineEntity> parseAddType() {
        List<WipReqLineEntity> resultList = new ArrayList<>();
        if (StringUtils.isBlank(this.getMoLotNo())) {
            List<WipLotVO> wipLotList = wipLotRepository.selectByHeaderId(this.getAimHeaderId());
            if (ListUtil.empty(wipLotList)) {
                throw new ServerException(ReqInsErrEnum.ADD_LOT_NULL.getCode(), ReqInsErrEnum.ADD_LOT_NULL.getDesc() + this.toString());
            }
            resultList.addAll(wipLotList.stream().map(lot -> this.buildReqLine().setLotNumber(lot.getLotNumber())).collect(Collectors.toList()));
        } else {
            resultList.add(this.buildReqLine());
        }
        return resultList;
    }

    private List<WipReqLineEntity> parseDeleteType(Map<String, List<WipReqLineEntity>> reqLineMap) {
        List<WipReqLineEntity> reqLineList = reqLineMap.get(this.getInsDetailId());
        reqLineList.forEach(line -> line.setChangeType(ChangedTypeEnum.DELETE.getCode()));
        return reqLineMap.get(this.getInsDetailId());
    }

    private List<WipReqLineEntity> parseReplaceType(Map<String, List<WipReqLineEntity>> reqLineMap) {
        if (StringUtils.isBlank(this.getItemIdNew())) {
            throw new ServerException(ReqInsErrEnum.KEY_NULL.getCode(), ReqInsErrEnum.KEY_NULL.getDesc() + "替换后物料不可为空,指令:" + this.toString());
        }
        String afterItemNo = itemService.getItemNo(this.getOrganizationId(), this.getItemIdNew());
        List<WipReqLineEntity> reqLineList = reqLineMap.get(this.getInsDetailId());
        reqLineList.forEach(line ->
                line.setBeforeItemNo(line.getItemNo())
                .setItemNo(afterItemNo)
                .setChangeType(ChangedTypeEnum.REPLACE.getCode()));
        return reqLineList;
    }

    @Override
    public String toString() {
        List<String> fieldPrintList = new ArrayList<>();
        BiConsumer<String, String> addNonNull = (p, v) -> {
            if (StringUtils.isNotBlank(v)) {
                fieldPrintList.add(p + "=" + v);
            }
        };
        addNonNull.accept("投料单头", this.getAimHeaderId());
        addNonNull.accept("组织", this.getOrganizationId());
        addNonNull.accept("批次", this.getMoLotNo());
        addNonNull.accept("工序", this.getWkpNo());
        addNonNull.accept("物料ID", this.getItemIdNew());
        addNonNull.accept("位号", this.getPosNo());
        addNonNull.accept("变更类型", this.getOperationTypeDesc());
        return String.join(",", fieldPrintList);
    }

    public String getOperationTypeDesc() {
        InsOperationTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(this.getOperationType(), InsOperationTypeEnum.class);
        if (Objects.isNull(typeEnum)) {
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), String.format("指令ID:%s变更类型为空或无法识别", this.getInsDetailId()));
        }
        return typeEnum.getDesc();
    }

    public static ReqInsDetailEntity get() {
        return DomainFactory.get(ReqInsDetailEntity.class);
    }

}
