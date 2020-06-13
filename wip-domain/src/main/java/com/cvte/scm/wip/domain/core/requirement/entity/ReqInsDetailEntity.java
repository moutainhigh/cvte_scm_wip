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
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInsDetailEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsDetailRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipLotRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsDetailBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    // 冗余目标投料单批次
    private String aimReqLotNo;

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

    public void deleteInsDetail() {
        this.setInsStatus(StatusEnum.CLOSE.getCode());
        if (StringUtils.isBlank(this.getInvalidBy())) {
            this.setInvalidBy(EntityUtils.getWipUserId());
        }
        detailRepository.update(this);
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
            entity.deleteInsDetail();
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
        List<WipLotVO> wipLotList = wipLotRepository.selectByHeaderId(this.getAimHeaderId());
        Map<String, WipLotVO> wipLotMap = new HashMap<>();
        wipLotList.forEach(lot -> wipLotMap.put(lot.getLotNumber(), lot));
        List<WipReqLineEntity> reqLineList = reqLineMap.get(this.getInsDetailId());
        switch (typeEnum) {
            case ADD:
                return this.parseAddType(wipLotMap);
            case DELETE:
                return this.parseDeleteType(reqLineList);
            case REPLACE:
                return this.parseReplaceType(reqLineList);
            case REDUCE:
                return this.parseReduceType(reqLineList, wipLotMap);
            case INCREASE:
                return this.parseIncreaseType(reqLineList, wipLotMap);
        }
        return Collections.emptyList();
    }

    private WipReqLineEntity buildReqLine(WipLotVO wipLotVO) {
        WipReqLineEntity reqLine = new WipReqLineEntity();
        reqLine.setHeaderId(this.getAimHeaderId())
                .setOrganizationId(this.getOrganizationId())
                .setWkpNo(this.getWkpNo())
                .setPosNo(this.getPosNo())
                .setItemId(this.getItemIdNew())
                .setItemNo(itemService.getItemNo(this.getOrganizationId(), this.getItemIdNew()))
                .setUnitQty(0.0)
                .setReqQty(0)
                .setLotNumber(wipLotVO.getLotNumber())
                .setChangeType(ChangedTypeEnum.ADD.getCode());
        return reqLine;
    }

    private List<WipReqLineEntity> parseAddType(Map<String, WipLotVO> wipLotMap) {
        List<WipReqLineEntity> resultList = new ArrayList<>();
        if (StringUtils.isBlank(this.getMoLotNo()) || this.getAimReqLotNo().equals(this.getMoLotNo())) {
            // 小批次为空 或者 小批次=工单批次, 则新增所有批次
            if (Objects.isNull(wipLotMap) || wipLotMap.isEmpty()) {
                throw new ServerException(ReqInsErrEnum.ADD_LOT_NULL.getCode(), ReqInsErrEnum.ADD_LOT_NULL.getDesc() + this.toString());
            }
            resultList.addAll(wipLotMap.values().stream().map(this::buildReqLine).collect(Collectors.toList()));
        } else {
            // 否则只生成小批次
            WipLotVO filterWipLot = wipLotMap.get(this.getMoLotNo());
            if (Objects.isNull(filterWipLot)) {
                // 若小批次不存在, 报错
                throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc() + this.toString());
            }
            // 只会筛选出一个有效的小批次
            resultList.add(this.buildReqLine(filterWipLot));
        }
        if (StringUtils.isBlank(this.getWkpNo())) {
            // 如果工序为空, 则设置为默认工序10
            resultList.forEach(line -> line.setWkpNo("10"));
        }
        return this.allocateQty(resultList, wipLotMap, this.getItemUnitQty());
    }

    private List<WipReqLineEntity> parseDeleteType(List<WipReqLineEntity> reqLineList) {
        reqLineList.forEach(line -> line.setChangeType(ChangedTypeEnum.DELETE.getCode()));
        return reqLineList;
    }

    private List<WipReqLineEntity> parseReplaceType(List<WipReqLineEntity> reqLineList) {
        if (StringUtils.isBlank(this.getItemIdNew())) {
            throw new ServerException(ReqInsErrEnum.KEY_NULL.getCode(), ReqInsErrEnum.KEY_NULL.getDesc() + "替换后物料不可为空,指令:" + this.toString());
        }
        String afterItemNo = itemService.getItemNo(this.getOrganizationId(), this.getItemIdNew());
        reqLineList.forEach(line ->
                line.setBeforeItemNo(line.getItemNo())
                .setItemNo(afterItemNo)
                .setChangeType(ChangedTypeEnum.REPLACE.getCode()));
        return reqLineList;
    }

    private List<WipReqLineEntity> parseReduceType(List<WipReqLineEntity> reqLineList, Map<String, WipLotVO> wipLotMap) {
        return this.allocateQty(reqLineList, wipLotMap, this.getItemUnitQty().negate());
    }

    @VisibleForTesting
    List<WipReqLineEntity> parseIncreaseType(List<WipReqLineEntity> reqLineList, Map<String, WipLotVO> wipLotMap) {
        if (ListUtil.empty(reqLineList)) {
            // 如果投料行为空, 则新增
            return parseAddType(wipLotMap);
        }
        return this.allocateQty(reqLineList, wipLotMap, this.getItemUnitQty());
    }

    /**
     * 将批次数量分配到投料行上, 单位用量可为负, 为负时扣减数量
     * @since 2020/6/13 10:54 上午
     * @author xueyuting
     * @param reqLineList 投料行
     * @param wipLotMap 工单批次
     * @param updateUnitQty 单位用量
     */
    @VisibleForTesting
    List<WipReqLineEntity> allocateQty(List<WipReqLineEntity> reqLineList, Map<String, WipLotVO> wipLotMap, BigDecimal updateUnitQty) {
        List<WipReqLineEntity> resultList = new ArrayList<>();

        // 小批次数量之和(工单数量) * 单位用量, 以这个数量为总量来分配
        BigDecimal remainLotQty = wipLotMap.values().stream().map(WipLotVO::getLotQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(updateUnitQty)
                .setScale(1, RoundingMode.FLOOR)
                .setScale(0, RoundingMode.CEILING);

        Map<String, List<WipReqLineEntity>> lotGroupLineMap = reqLineList.stream().collect(Collectors.groupingBy(WipReqLineEntity::getLotNumber));
        Iterator<Map.Entry<String, List<WipReqLineEntity>>> lotGroupLineMapIterator = lotGroupLineMap.entrySet().iterator();
        while (lotGroupLineMapIterator.hasNext()) {
            Map.Entry<String, List<WipReqLineEntity>> lotGroupLineEntry = lotGroupLineMapIterator.next();
            String lotNumber = lotGroupLineEntry.getKey();
            WipLotVO wipLot = wipLotMap.get(lotNumber);
            if (Objects.isNull(wipLot)) {
                // 小批次不存在, 报错
                throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc() + this.toString());
            }
            BigDecimal updateQty = updateUnitQty.multiply(wipLot.getLotQuantity()).setScale(1, RoundingMode.DOWN).setScale(0, RoundingMode.UP);
            if (!lotGroupLineMapIterator.hasNext()) {
                updateQty = remainLotQty;
            }
            // 工单数量扣减已分配到该小批次上的数量
            remainLotQty = remainLotQty.subtract(updateQty);

            List<WipReqLineEntity> lotGroupLineList = lotGroupLineEntry.getValue();
            // 分配策略: 将分配数量均匀加/减到批次对应的投料行上, 若出现小数, 向上取整
            BigDecimal allocateQty = updateQty.divide(new BigDecimal(lotGroupLineList.size()), 0, RoundingMode.UP);
            Iterator<WipReqLineEntity> lotGroupLineIterator = lotGroupLineList.iterator();
            while (lotGroupLineIterator.hasNext()) {
                WipReqLineEntity lotGroupLine = lotGroupLineIterator.next();
                // 计算更新后的需求数量
                if (!lotGroupLineIterator.hasNext()) {
                    // 把剩余的数量都分配到最后一个行上
                    allocateQty = updateQty;
                }
                int resultQty = lotGroupLine.getReqQty() + allocateQty.intValue();

                BigDecimal realAllocateQty = allocateQty;
                if (resultQty < 0) {
                    // 可能减少到0, 此时实际分配数量 = 需求数量
                    if (allocateQty.compareTo(BigDecimal.ZERO) >= 0) {
                        realAllocateQty = new BigDecimal(lotGroupLine.getReqQty());
                    } else {
                        realAllocateQty = new BigDecimal(-lotGroupLine.getReqQty());
                    }
                    resultQty = 0;
                }
                lotGroupLine.setReqQty(resultQty);

                // 单位用量 = 更新后需求数量 / 工单批次数量, 非最后一个, 向接近0的方向取整
                RoundingMode roundingMode = RoundingMode.UP;
                if (!lotGroupLineIterator.hasNext()) {
                    // 最后一行 向远离0的方向取整
                    roundingMode = RoundingMode.DOWN;
                }
                lotGroupLine.setUnitQty(new BigDecimal(resultQty).divide(wipLot.getLotQuantity(), 6, roundingMode).doubleValue());
                // 小批次数量扣减已分配到位号的数量
                updateQty = updateQty.subtract(realAllocateQty);
            }
            resultList.addAll(lotGroupLineList);
        }
        for (WipReqLineEntity line : resultList) {
            if (StringUtils.isBlank(line.getChangeType())) {
                // 变更类型为"更新"
                line.setChangeType(ChangedTypeEnum.UPDATE.getCode());
            }
        }
        return resultList;
    }

    public void processSuccess() {
        this.setInsStatus(ProcessingStatusEnum.SOLVED.getCode());
        this.setConfirmedBy(EntityUtils.getWipUserId());
        this.setConfirmDate(new Date());
        detailRepository.update(this);
    }

    public void batchProcessSuccess(List<ReqInsDetailEntity> insDetailList) {
        for (ReqInsDetailEntity insDetail : insDetailList) {
            insDetail.processSuccess();
        }
    }

    public void processFailed() {
        this.setInsStatus(ProcessingStatusEnum.EXCEPTION.getCode());
        detailRepository.update(this);
    }

    public void batchProcessFailed(List<ReqInsDetailEntity> insDetailList) {
        for (ReqInsDetailEntity insDetail : insDetailList) {
            insDetail.processFailed();
        }
    }

    @Override
    public String toString() {
        List<String> fieldPrintList = new ArrayList<>();
        BiConsumer<String, String> addNonNull = (p, v) -> {
            if (StringUtils.isNotBlank(v)) {
                fieldPrintList.add(p + "=" + v);
            }
        };
        String itemNoNew = null;
        if (StringUtils.isNotBlank(this.getItemIdNew())) {
            itemNoNew = itemService.getItemNo(this.getOrganizationId(), this.getItemIdNew());
        }
        addNonNull.accept("批次", this.getMoLotNo());
        addNonNull.accept("工序", this.getWkpNo());
        addNonNull.accept("物料", itemNoNew);
        addNonNull.accept("位号", this.getPosNo());
        addNonNull.accept("变更类型", this.getOperationTypeDesc());
        return String.join(",", fieldPrintList);
    }

    private String getOperationTypeDesc() {
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
