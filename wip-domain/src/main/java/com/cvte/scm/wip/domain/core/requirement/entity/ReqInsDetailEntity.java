package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
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
import org.springframework.beans.BeanUtils;
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
    private WipLotRepository wipLotRepository;

    public ReqInsDetailEntity(ReqInsDetailRepository detailRepository, WipLotRepository wipLotRepository) {
        this.detailRepository = detailRepository;
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

    private String executeResult;

    // 冗余目标投料单头ID
    private String aimHeaderId;

    // 冗余目标投料单批次
    private String aimReqLotNo;

    private String issueFlag;

    private String itemNoOld;

    private String itemNoNew;

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
        // 可更新列表
        List<ReqInsDetailBuildVO> updateVoList = new ArrayList<>(detailVoList);
        if (ListUtil.notEmpty(dbDetailEntityList)) {
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
            } else {
                resultDetailEntityList.addAll(dbDetailEntityList);
            }
        }

        // 剩下的新增
        if (updateVoList.size() != detailVoList.size()) {
            Set<String> savedSourceIdSet = updateVoList.stream().map(ReqInsDetailBuildVO::getSourceChangeDetailId).collect(Collectors.toSet());
            detailVoList.removeIf(detailVo -> savedSourceIdSet.contains(detailVo.getSourceChangeDetailId()));
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
                .setItemNo(this.getItemNoNew())
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
                throw new ServerException(ReqInsErrEnum.TARGET_LOT_INVALID.getCode(), ReqInsErrEnum.TARGET_LOT_INVALID.getDesc());
            }
            resultList.addAll(wipLotMap.values().stream().map(this::buildReqLine).collect(Collectors.toList()));
        } else {
            // 否则只生成小批次
            WipLotVO filterWipLot = wipLotMap.get(this.getMoLotNo());
            if (Objects.isNull(filterWipLot)) {
                // 若小批次不存在, 报错
                throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc());
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
            throw new ServerException(ReqInsErrEnum.KEY_NULL.getCode(), ReqInsErrEnum.KEY_NULL.getDesc() + "替换后物料不可为空");
        }
        reqLineList.forEach(line ->
                line.setBeforeItemNo(line.getItemNo())
                .setItemNo(this.getItemNoNew())
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

        // 用于分配给小批次的总数量
        BigDecimal remainLotQty;
        if (Objects.nonNull(this.getItemQty())) {
            // 用量不为空时取用量
            remainLotQty = this.getItemQty();
            if (updateUnitQty.compareTo(BigDecimal.ZERO) < 0) {
                remainLotQty = remainLotQty.negate();
            }
        } else {
            List<String> lineLotNumberList = reqLineList.stream().map(WipReqLineEntity::getLotNumber).collect(Collectors.toList());
            Iterator<Map.Entry<String, WipLotVO>> wipLotIterator = wipLotMap.entrySet().iterator();
            while (wipLotIterator.hasNext()) {
                Map.Entry<String, WipLotVO> wipLotEntry = wipLotIterator.next();
                String lotNumber = wipLotEntry.getKey();
                if (!lineLotNumberList.contains(lotNumber)) {
                    wipLotIterator.remove();
                }
            }
            if (CollectionUtils.isEmpty(wipLotMap)) {
                throw new ServerException(ReqInsErrEnum.TARGET_LOT_INVALID.getCode(), ReqInsErrEnum.TARGET_LOT_INVALID.getDesc());
            }

            // 用量为空时取 小批次数量之和(工单数量) * 单位用量
            remainLotQty = wipLotMap.values().stream().map(WipLotVO::getLotQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(updateUnitQty)
                    .setScale(1, RoundingMode.FLOOR)
                    .setScale(0, RoundingMode.CEILING);
        }

        Map<String, List<WipReqLineEntity>> lotGroupLineMap = reqLineList.stream().collect(Collectors.groupingBy(WipReqLineEntity::getLotNumber));
        Iterator<Map.Entry<String, List<WipReqLineEntity>>> lotGroupLineMapIterator = lotGroupLineMap.entrySet().iterator();
        while (lotGroupLineMapIterator.hasNext()) {
            Map.Entry<String, List<WipReqLineEntity>> lotGroupLineEntry = lotGroupLineMapIterator.next();
            String lotNumber = lotGroupLineEntry.getKey();
            WipLotVO wipLot = wipLotMap.get(lotNumber);
            if (Objects.isNull(wipLot)) {
                // 小批次不存在, 报错
                throw new ServerException(ReqInsErrEnum.TARGET_LOT_INVALID.getCode(), ReqInsErrEnum.TARGET_LOT_INVALID.getDesc());
            }
            // 向上取整的原因: 分配数量可以是负数, 如果向下取整, 可能出现前几个小批次刚好没扣完, 而分配到最后一个数量又过剩, 不合理
            // 如: 3个小批次, 用量各为334, 分配总量1001, -333.37向下取整为-333, 最后一个-335, 结果是1,1,0;而向上取整的结果是0,0,1
            BigDecimal updateQty = updateUnitQty.multiply(wipLot.getLotQuantity()).setScale(1, RoundingMode.DOWN).setScale(0, RoundingMode.UP);
            if (!lotGroupLineMapIterator.hasNext()) {
                updateQty = remainLotQty;
            }
            // 工单数量扣减已分配到该小批次上的数量
            remainLotQty = remainLotQty.subtract(updateQty);

            List<WipReqLineEntity> lotGroupLineList = lotGroupLineEntry.getValue();
            // 因为原子服务update的回写EBS不会被处理, 所以要同时生成新增/删除类型的参数用于回写EBS的行
            List<WipReqLineEntity> reduceOrIncreaseList = new ArrayList<>();

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

                // 若行变更类型为空(以防新增类型重复处理), 则再生成一个变更类型=指令的行, 用于回写EBS, 原因是EBS接口表不处理update类型的数据
                if (StringUtils.isBlank(lotGroupLine.getChangeType())) {
                    WipReqLineEntity reduceOrIncreaseLine = new WipReqLineEntity();
                    BeanUtils.copyProperties(lotGroupLine, reduceOrIncreaseLine);
                    reduceOrIncreaseLine.setReqQty(realAllocateQty.abs().intValue())
                            .setUnitQty(realAllocateQty.abs().divide(wipLot.getLotQuantity(), 6, roundingMode).doubleValue());
                    if (InsOperationTypeEnum.REDUCE.getCode().equals(this.getOperationType())) {
                        reduceOrIncreaseLine.setChangeType(ChangedTypeEnum.REDUCE.getCode());
                    } else {
                        reduceOrIncreaseLine.setChangeType(ChangedTypeEnum.INCREASE.getCode());
                    }
                    reduceOrIncreaseList.add(reduceOrIncreaseLine);
                }

                // 小批次数量扣减已分配到位号的数量
                updateQty = updateQty.subtract(realAllocateQty);
            }
            resultList.addAll(lotGroupLineList);
            resultList.addAll(reduceOrIncreaseList);
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
        this.setExecuteResult("成功");
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
        addNonNull.accept("批次", this.getMoLotNo());
        addNonNull.accept("工序", this.getWkpNo());
        addNonNull.accept("物料", this.getItemNoNew());
        addNonNull.accept("位号", this.getPosNo());
        addNonNull.accept("变更类型", this.getOperationTypeDesc());
        return String.join(",", fieldPrintList);
    }

    private String getOperationTypeDesc() {
        InsOperationTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(this.getOperationType(), InsOperationTypeEnum.class);
        if (Objects.isNull(typeEnum)) {
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), "无效的变更类型");
        }
        return typeEnum.getDesc();
    }

    public static ReqInsDetailEntity get() {
        return DomainFactory.get(ReqInsDetailEntity.class);
    }

}
